/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: tfs_session.cpp 49 2010-11-16 09:58:57Z zongdai@taobao.com $
 *
 * Authors:
 *   duolong <duolong@taobao.com>
 *      - initial release
 *
 */
#include <tbsys.h>
#include <Memory.hpp>
#include "tfs_session.h"
#include "message/client_manager.h"
#include "message/client.h"
#include "message/client_pool.h"

using namespace tfs::client;
using namespace tfs::common;
using namespace tfs::message;
using namespace std;

TfsSession::TfsSession(const std::string& nsip, const int32_t cache_time, const int32_t cache_items)
  :ns_addr_(0), ns_addr_str_(nsip), block_cache_time_(cache_time), block_cache_items_(cache_items),
		cluster_id_(0), use_cache_(USE_CACHE_FLAG_YES)
{
}

TfsSession::~TfsSession()
{
  block_cache_map_.clear();
}

int TfsSession::initialize()
{
  int ret = TFS_SUCCESS;
  if ((ns_addr_str_.empty()) || (ns_addr_str_.compare(" ") == 0))
  {
    TBSYS_LOG(ERROR, "nameserver address %s invalid", ns_addr_str_.c_str());
    ret = TFS_ERROR;
  }

  if (TFS_SUCCESS == ret)
  {
    ns_addr_ = Func::get_host_ip(ns_addr_str_.c_str());
    if (ns_addr_ <= 0)
    {
      TBSYS_LOG(ERROR, "nameserver address invalid", ns_addr_str_.c_str());
      ret = TFS_ERROR;
    }

    if (TFS_SUCCESS == ret)
    {
      ret = get_cluster_id_from_ns();
    }
  }

  return ret;
}

int TfsSession::get_block_info(uint32_t& block_id, VUINT64& rds, int32_t flag)
{
  int ret = TFS_SUCCESS;
  // insert to cache
  if (flag & T_WRITE)
  {
    if (!(flag & T_NOLEASE)) // write, no unlink
    {
      flag |= T_CREATE;
    }
    ret = get_block_info_ex(block_id, rds, flag);
  }
  else // read
  {
    if (0 == block_id)
    {
      TBSYS_LOG(ERROR, "blockid zero error for blockid: %u, mode: %d", block_id, flag);
      ret = TFS_ERROR;
    }
    else
    {
      // search in the local cache
      bool flag = false;
      if (USE_CACHE_FLAG_YES == use_cache_)
      {
        tbutil::Mutex::Lock lock(mutex_);
        BlockCache* block_cache = block_cache_map_.find(block_id);
        if (block_cache &&
            (block_cache->last_time_ >= time(NULL) - block_cache_time_))
        {
          TBSYS_LOG(DEBUG, "cache hit, blockid: %u", block_id);
          rds = block_cache->ds_;
          flag = true;
        }
      }

      if (!flag)
      {
        TBSYS_LOG(DEBUG, "cache miss, blockid: %u", block_id);
        if ((ret = get_block_info_ex(block_id, rds, T_READ)) == TFS_SUCCESS)
        {
          if (rds.size() <= 0)
          {
            TBSYS_LOG(ERROR, "get block %u info failed, dataserver size %u <= 0", block_id, rds.size());
            ret = TFS_ERROR;
          }
          else
          {
            insert_block_cache(block_id, rds);
          }
        }
      }
    }
  }
  return ret;
}

int TfsSession::get_block_info(SEG_DATA_LIST& seg_list, int32_t flag)
{
  int ret = TFS_SUCCESS;
  if (flag & T_WRITE)
  {
    if (!(flag & T_NOLEASE)) // write, no unlink
    {
      flag |= T_CREATE;
    }
    ret = get_block_info_ex(seg_list, flag);
  }
  else
  {
    TBSYS_LOG(DEBUG, "get block info for read. seg size: %d", seg_list.size());
    bool found = false;
    // search in the local cache
    if (USE_CACHE_FLAG_YES == use_cache_)
    {
      size_t block_count = 0;
      for (size_t i = 0; i < seg_list.size(); i++)
      {
        uint32_t block_id = seg_list[i]->seg_info_.block_id_;

        if (0 == block_id)
        {
          TBSYS_LOG(ERROR, "block zero error for mode: %d", block_id, flag);
          ret = TFS_ERROR;
          break;
        }

        tbutil::Mutex::Lock lock(mutex_);
        BlockCache* block_cache = block_cache_map_.find(block_id);
        if (block_cache &&
            (block_cache->last_time_ >= time(NULL) - block_cache_time_))
        {
          seg_list[i]->ds_ = block_cache->ds_;
          seg_list[i]->status_ = SEG_STATUS_OPEN_OVER;
          block_count++;
        }
      }
      if (block_count == seg_list.size())
      {
        TBSYS_LOG(DEBUG, "all block id cached");
        found = true;
      }
    }

    if (TFS_SUCCESS == ret && !found)
    {
      //BlockCache block_cache;
      if ((ret = get_block_info_ex(seg_list, T_READ)) == TFS_SUCCESS)
      {
        for (size_t i = 0; i < seg_list.size(); i++)
        {
          if (seg_list[i]->ds_.size() <= 0)
          {
            TBSYS_LOG(ERROR, "get block %u info failed, dataserver size %u <= 0",
                seg_list[i]->seg_info_.block_id_, seg_list[i]->ds_.size());
            ret = TFS_ERROR;
            break;
          }
          else
          {
            insert_block_cache(seg_list[i]->seg_info_.block_id_, seg_list[i]->ds_);
          }
        }
      }
    }
  }
  TBSYS_LOG(DEBUG, "batch get block info end, ret: %d", ret);
  return ret;
}

int TfsSession::get_block_info_ex(uint32_t& block_id, VUINT64& rds, const int32_t flag)
{
  GetBlockInfoMessage gbi_message(flag);
  gbi_message.set_block_id(block_id);

  Message* rsp = NULL;
  int ret = NewClientManager::get_instance()->call(ns_addr_, &gbi_message, WAIT_TIME_OUT, rsp);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(ERROR, "get cluster id from ns failed, ret: %d", ret);
  }
  else if (SET_BLOCK_INFO_MESSAGE == rsp->get_message_type()) //rsp will not be null
  {
    ret = TFS_ERROR;
    SetBlockInfoMessage* block_info_msg = dynamic_cast<SetBlockInfoMessage*>(rsp);
    rds = block_info_msg->get_block_ds();
    block_id = block_info_msg->get_block_id();
    if (rds.size() && block_id > 0)
    {
      if (block_info_msg->get_has_lease())
      {
        rds.push_back(ULONG_LONG_MAX);
        rds.push_back(block_info_msg->get_block_version());
        rds.push_back(block_info_msg->get_lease_id());
      }
      ret = TFS_SUCCESS;
    }
  }
  else
  {
    ret = EXIT_UNKNOWN_MSGTYPE;
    if (STATUS_MESSAGE == rsp->get_message_type())
    {
      TBSYS_LOG(ERROR, "get block %d info fail, ret: %d, error: %s, status: %d",
                block_id, ret, dynamic_cast<StatusMessage*>(rsp)->get_error(), dynamic_cast<StatusMessage*>(rsp)->get_status());
    }
    else
    {
      TBSYS_LOG(ERROR, "get block %d info fail, ret: %d, msg type: %d",
                block_id, ret, rsp->get_message_type());
    }
  }
  tbsys::gDelete(rsp);
  return ret;
}

int TfsSession::get_block_info_ex(SEG_DATA_LIST& seg_list, const int32_t flag)
{
  size_t block_count = 0;
  BatchGetBlockInfoMessage bgbi_message(flag);
  if (flag & T_WRITE)
  {
    block_count = seg_list.size();
    bgbi_message.set_block_count(block_count);
  }
  else
  {
    for (size_t i = 0; i < seg_list.size(); i++)
    {
      if (seg_list[i]->ds_.size() == 0)
      {
        bgbi_message.add_block_id(seg_list[i]->seg_info_.block_id_);
        block_count++;
        TBSYS_LOG(DEBUG, "get blockinfo add getblock message, blockid: %d, count: %d", seg_list[i]->seg_info_.block_id_, block_count);
      }
    }
  }

  Message* rsp = NULL;
  int ret = NewClientManager::get_instance()->call(ns_addr_, &bgbi_message, WAIT_TIME_OUT, rsp);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(ERROR, "get blockinfo failed, ret: %d", ret);
  }
  else if (BATCH_SET_BLOCK_INFO_MESSAGE == rsp->get_message_type())
  {
    BatchSetBlockInfoMessage* block_info_msg = dynamic_cast<BatchSetBlockInfoMessage*>(rsp);
    map<uint32_t, BlockInfoSeg>& block_info = block_info_msg->get_infos();
    if (block_info.size() != block_count)
    {
      TBSYS_LOG(ERROR, "batch get block info fail, get count conflict, request: %d, response: %u",
                block_count, block_info.size());
      ret = TFS_ERROR;
    }
    else
    {
      std::map<uint32_t, BlockInfoSeg>::iterator it;
      if (flag & T_READ)
      {
        for (size_t i = 0; i < seg_list.size(); ++i)
        {
          if ((it = block_info.find(seg_list[i]->seg_info_.block_id_)) == block_info.end() && seg_list[i]->ds_.empty())
          {
            TBSYS_LOG(ERROR, "get block %d info fail, blockinfo size: %d", seg_list[i]->seg_info_.block_id_, block_info.size());
            ret = TFS_ERROR;
            break;
          }

          if (it != block_info.end())
          {
            // ds_ will not be empty
            seg_list[i]->ds_ = it->second.ds_;
            seg_list[i]->status_ = SEG_STATUS_OPEN_OVER;
            TBSYS_LOG(DEBUG, "get block %u info, ds size: %d, return size: %d",
                seg_list[i]->seg_info_.block_id_, seg_list[i]->ds_.size(), it->second.ds_.size());
          }
        }
      }
      else if (flag & T_WRITE)
      {
        it = block_info.begin();
        TBSYS_LOG(DEBUG, "get write block block count: %d, seg list size: %d", block_info.size(), seg_list.size());

        for (size_t i = 0; i < seg_list.size(); i++, it++)
        {
          seg_list[i]->seg_info_.block_id_ = it->first;
          seg_list[i]->ds_ = it->second.ds_;
          seg_list[i]->status_ = SEG_STATUS_OPEN_OVER;

          TBSYS_LOG(DEBUG, "get write block %u success, ds list size: %d", seg_list[i]->seg_info_.block_id_, seg_list[i]->ds_.size());
          if (it->second.has_lease_) // should have
          {
            seg_list[i]->ds_.push_back(ULONG_LONG_MAX);
            seg_list[i]->ds_.push_back(it->second.version_);
            seg_list[i]->ds_.push_back(it->second.lease_);
            TBSYS_LOG(DEBUG, "get write block %u success, ds list size: %d, version: %d, lease: %d",
                seg_list[i]->seg_info_.block_id_, seg_list[i]->ds_.size(),
                it->second.version_, it->second.lease_);
          }
        }
      }
      else
      {
        TBSYS_LOG(ERROR, "unknown flag %d", flag);
        ret = TFS_ERROR;
      }
    }
  }
  else
  {
    ret = EXIT_UNKNOWN_MSGTYPE;
    if (STATUS_MESSAGE == rsp->get_message_type())
    {
      TBSYS_LOG(ERROR, "get batch block info fail, ret: %d, error: %s, status: %d",
                ret, dynamic_cast<StatusMessage*>(rsp)->get_error(), dynamic_cast<StatusMessage*>(rsp)->get_status());
    }
    else
    {
      TBSYS_LOG(ERROR, "get batch block info fail, ret: %d, msg type: %d",
                ret, rsp->get_message_type());
    }
  }

  tbsys::gDelete(rsp);
  return ret;
}

int TfsSession::get_cluster_id_from_ns()
{
  ClientCmdMessage cc_message;
  cc_message.set_type(CLIENT_CMD_SET_PARAM);
  cc_message.set_block_id(20);

  Message* rsp = NULL;
  int ret = NewClientManager::get_instance()->call(ns_addr_, &cc_message, WAIT_TIME_OUT, rsp);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(ERROR, "get block info failed, ret: %d", ret);
  }
  else if (STATUS_MESSAGE == rsp->get_message_type())
  {
    StatusMessage* status_msg = dynamic_cast<StatusMessage*>(rsp);
    //ugly use error msg
    if (status_msg->get_status() == STATUS_MESSAGE_OK &&
        strlen(status_msg->get_error()) > 0)
    {
      char cluster_id = static_cast<char> (atoi(status_msg->get_error()));
      if (isdigit(cluster_id) || isalpha(cluster_id))
      {
        cluster_id_ = cluster_id - '0';
        TBSYS_LOG(INFO, "get cluster id from nameserver success. cluster id: %d", cluster_id_);
      }
      else
      {
        TBSYS_LOG(ERROR, "get cluster id from nameserver fail. cluster id: %c", cluster_id);
        ret = TFS_ERROR;
      }
    }
  }
  else
  {
    TBSYS_LOG(ERROR, "get cluster id from ns failed, msg type error. type: %d", rsp->get_message_type());
    ret = EXIT_UNKNOWN_MSGTYPE;
  }

  tbsys::gDelete(rsp);
  return ret;
}

void TfsSession::insert_block_cache(const uint32_t block_id, const VUINT64& rds)
{
  if (USE_CACHE_FLAG_YES == use_cache_)
  {
    TBSYS_LOG(DEBUG, "cache insert, blockid: %u", block_id);
    BlockCache block_cache;
    block_cache.last_time_ = time(NULL);
    block_cache.ds_ = rds;
    tbutil::Mutex::Lock lock(mutex_);
    block_cache_map_.insert(block_id, block_cache);
  }
}

void TfsSession::remove_block_cache(const uint32_t block_id)
{
  if (USE_CACHE_FLAG_YES == use_cache_)
  {
    TBSYS_LOG(DEBUG, "cache remove, blockid: %u", block_id);
    tbutil::Mutex::Lock lock(mutex_);
    block_cache_map_.remove(block_id);
  }
}
