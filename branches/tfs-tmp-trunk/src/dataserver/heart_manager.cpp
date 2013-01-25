/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: heart_manager.cpp 983 2012-01-15 09:59:33Z duanfei $
 *
 * Authors:
 *   duanfei<duanfei@taobao.com>
 *      - initial release
 *
 */
#include <tbsys.h>
#include <Memory.hpp>
#include <Mutex.h>
#include "ns_define.h"
#include "nameserver.h"
#include "common/error_msg.h"
#include "common/config_item.h"
#include "common/client_manager.h"
#include "heart_manager.h"
#include "global_factory.h"

using namespace tfs::common;
using namespace tfs::message;
using namespace tbsys;

namespace tfs
{
  namespace nameserver
  {
    static const int32_t SEND_BLOCK_TO_NS_PARAMETER_ERROR = -1;
    static const int32_t SEND_BLOCK_TO_NS_CREATE_NETWORK_CLIENT_ERROR = -2;
    static const int32_t SEND_BLOCK_TO_NS_NETWORK_ERROR = -3;
    DataServerHeartManager::DataServerHeartManager(DataService& service, const std::vector<uint64_t>& ns_ip_port):
      service_(service)
    {
      assert(NULL != ns_ip_port);
      assert(ns_ip_port_length <= MAX_SINGLE_CLUSTER_NS_NUM);
      data_server_info_.status_ = DATASERVER_STATUS_DEAD;
      std::vector<uint64_t>::iterator iter = ns_ip_port.begin();
      for (int32_t index = 0; iter != ns_ip_port.end(); ++iter,++index)
        ns_ip_port_[index] = (*iter);
      for (int32_t i = 0; i < MAX_SINGLE_CLUSTER_NS_NUM; ++i)
        heart_beat_thread_[i] = 0;
    }

    DataServerHeartManager::~DataServerHeartManager()
    {

    }

    int DataServerHeartManager::initialize()
    {
      data_server_info_.startup_time_ = time(NULL);
      IpAddr* adr = reinterpret_cast<IpAddr*>(&data_server_info_.id_);
      adr->ip_ = tbsys::CNetUtil::getAddr(service_.get_ip_addr());
      adr->port_ = service_.get_listen_port();
      for (int32_t index = 0; index < MAX_SINGLE_CLUSTER_NS_NUM; ++index)
      {
        heart_beat_thread_[index] = new (std::nothrow)HeartBeatThreadHelper(*this);
        assert(NULL != heart_beat_thread_[index]);
      }
      data_server_info_.status_ = DATASERVER_STATUS_ALIVE;
      return TFS_SUCCESS;
    }

    void DataServerHeartManager::wait_for_shut_down()
    {
      for (int32_t index = 0; index < MAX_SINGLE_CLUSTER_NS_NUM; ++index)
      {
        if (0 != heart_beat_thread_[index])
          heart_beat_thread_[index]->join();
      }
    }

    void DataServerHeartManager::HeartBeatThreadHelper::run()
    {
      int32_t ret = TFS_ERROR;
      const int32_t KEEPALIVE_TIMEOUT_MS = 2000;//2s
      int8_t heart_interval = DEFAULT_HEART_INTERVAL;
      //sleep for a while, waiting for listen port establish
      Func::sleep(heart_interval, stop_);
      TBSYS_LOG(INFO, "start heartbeat,nameserver: %s", tbsys::CNetUtil::addrToString(ns_ip_port_[who]).c_str());
      while (!DsRuntimeGlobalInformation::instance().is_destroyed())
      {
        if (0 == who % MAX_SINGLE_CLUSTER_NS_NUM)
        {
          DsRuntimeGlobalInformation& info = DsRuntimeGlobalInformation::instance();
          service_.get_block_manager().get_space(info.information_.total_capacity_, info.information_.total_capacity_);
          info.information_.block_count_ = service_.get_block_manager().get_all_logic_block_count();
          info.current_load_ = Func::get_load_avg();
          info.current_time_ = time(NULL);
        }
        int32_t sleep_time_us = 500000;
        heart_interval = DEFAULT_HEART_INTERVAL;
        ret = keepalive(heart_interval, who, KEEPALIVE_TIMEOUT_MS);
        if (TFS_SUCCESS != ret)
        {
          TBSYS_LOG(WARN, "dataserver keepalive failed, nameserver: %s, ret: %d",
              tbsys::CNetUtil::addrToString(ns_ip_port_[who]).c_str(), ret);
        }
        else
        {
          sleep_time_us = heart_interval <= 0 ? DEFAULT_HEART_INTERVAL * 1000000 : heart_interval * 1000000;
        }
        usleep(sleep_time_us);
      }
      data_server_info_.status_ = DATASERVER_STATUS_DEAD;
      keepalive(heart_interval, who, KEEPALIVE_TIMEOUT_MS);
      TBSYS_LOG(INFO, "stop heartbeat,nameserver: %s", tbsys::CNetUtil::addrToString(ns_ip_port_[who]).c_str());
    }

    int DataServerHeartManager::keepalive(int8_t& heart_interval, const int32_t who, const int64_t timeout)
    {
      int ret = who < 0 || who >= MAX_SINGLE_CLUSTER_NS_NUM ? SEND_BLOCK_TO_NS_PARAMETER_ERROR : TFS_SUCCESS;
      if (TFS_SUCCESS == ret)
      {
        SetDataserverMessage req_msg;
        req_msg.set_dataserver_information(data_server_info_);
        tbnet::Packet* message = NULL;
        NewClient* client = NewClientManager::get_instance().create_client();
        ret = NULL != client ? TFS_SUCCESS : SEND_BLOCK_TO_NS_CREATE_NETWORK_CLIENT_ERROR;
        if (TFS_SUCCESS == ret)
        {
          ret = send_msg_to_server(ns_ip_port_[who], client, &req_msg, message, timeout);
          if (TFS_SUCCESS != ret)
          {
            TBSYS_LOG(INFO, "send_msg_to_server: %s failed, ret: %d", tbsys::CNetUtil::addrToString(ns_ip_port_[who]).c_str(), ret);
            ret = SEND_BLOCK_TO_NS_NETWORK_ERROR;
          }
          else
          {
            ret = RESP_HEART_MESSAGE == message->getPCode() ? TFS_SUCCESS : SEND_BLOCK_TO_NS_NETWORK_ERROR;
            if (TFS_SUCCESS == ret)
            {
              DsRuntimeGlobalInformation& info = DsRuntimeGlobalInformation::instance();
              RespHeartMessage* resp_hb_msg = dynamic_cast<RespHeartMessage*>(message);
              heart_interval = resp_hb_msg->get_heart_interval();
              info.max_mr_traffic_mb_ = resp_hb_msg->get_max_mr_network_capacity_mb();
              info.max_rw_traffic_mb_ = resp_hb_msg->get_max_rw_network_capacity_mb();
              int32_t status = resp_hb_msg->get_status();
              if (HEART_MESSAGE_OK != status)
              {
                TBSYS_LOG(WARN, "dataserver:%s keepalive failed,  nameserver: %s",
                    tbsys::CNetUtil::addrToString(data_server_info_.id_).c_str(), tbsys::CNetUtil::addrToString(ns_ip_port_[who]).c_str());
              }
            }
          }
        }
        NewClientManager::get_instance().destroy_client(client);
      }
      return ret;
   }
  }/** end namespace dataserver **/
} /** end namespace tfs **/
