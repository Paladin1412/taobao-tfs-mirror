/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: meta_server_service.h 49 2011-08-08 09:58:57Z nayan@taobao.com $
 *
 * Authors:
 *   daoan <daoan@taobao.com>
 *      - initial release
 *
 */

#ifndef TFS_METAWITHKV_META_KV_SERVICE_H_
#define TFS_METAWITHKV_META_KV_SERVICE_H_

#include "common/parameter.h"
#include "common/base_service.h"
#include "common/status_message.h"
#include "message/message_factory.h"

#include "meta_info_helper.h"
namespace tfs
{
  namespace metawithkv
  {
    class MetaKvService : public common::BaseService
    {
    public:
      MetaKvService();
      virtual ~MetaKvService();

    public:
      // override
      virtual tbnet::IPacketStreamer* create_packet_streamer();
      virtual void destroy_packet_streamer(tbnet::IPacketStreamer* streamer);
      virtual common::BasePacketFactory* create_packet_factory();
      virtual void destroy_packet_factory(common::BasePacketFactory* factory);
      virtual const char* get_log_file_path();
      virtual const char* get_pid_file_path();
      virtual bool handlePacketQueue(tbnet::Packet* packet, void* args);
      virtual int initialize(int argc, char* argv[]);
      virtual int destroy_service();

      int put_meta(/*put_meta_message* */);
      int get_meta(/*put_meta_message* */);


    private:
      DISALLOW_COPY_AND_ASSIGN(MetaKvService);

      MetaInfoHelper meta_info_helper_;
      //TODO add stat
      //StatInfoHelper stat_info_helper_;
    };
  }
}

#endif