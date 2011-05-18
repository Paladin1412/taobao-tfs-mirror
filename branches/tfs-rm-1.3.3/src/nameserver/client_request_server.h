/*
* (C) 2007-2010 Alibaba Group Holding Limited.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License version 2 as
* published by the Free Software Foundation.
*
*
* Version: $Id
*
* Authors:
*   daoan <daoan@taobao.com>
*      - initial release
*
*/
#ifndef TFS_NAMESERVER_CLIENT_REQUEST_SERVER_H_
#define TFS_NAMESERVER_CLIENT_REQUEST_SERVER_H_
#include "common/lock.h"
#include "block_chunk.h"
#include "block_collect.h"
#include "server_collect.h"
#include "lease_clerk.h"
#include "message/message.h"
#include "oplog_sync_manager.h"
namespace tfs
{
namespace nameserver
{
  struct CloseParameter
  {
    common::BlockInfo block_info_;
    uint64_t id_;
    uint32_t lease_id_;
    common::UnlinkFlag unlink_flag_;
    common::WriteCompleteStatus status_;
    bool need_new_;
    char error_msg_[256];
  };
  class LayoutManager;
  class ClientRequestServer
  {
    public:
      explicit ClientRequestServer(LayoutManager& lay_out_manager);

      int keepalive(const common::DataServerStatInfo&, const common::HasBlockFlag flag,
          common::BLOCK_INFO_LIST& blocks, common::VUINT32& expires, bool& need_sent_block);
      int open(uint32_t& block_id, const int32_t mode, uint32_t& lease_id, int32_t& version, common::VUINT64& ds_list);
      int batch_open(const common::VUINT32& blocks, const int32_t mode, const int32_t block_count, std::map<uint32_t, common::BlockInfoSeg>& out);

      int close(CloseParameter& param);
    private:

      int open_read_mode(const uint32_t block_id, common::VUINT64& readable_ds_list);
      int open_write_mode(const int32_t mode,
          uint32_t& block_id,
          uint32_t& lease_id,
          int32_t& version,
          common::VUINT64& ds_list);
      int batch_open_read_mode(const common::VUINT32& blocks, std::map<uint32_t, common::BlockInfoSeg>& out);
      int batch_open_write_mode(const int32_t mode, const int32_t block_count, std::map<uint32_t, common::BlockInfoSeg>& out);

    private:
      LayoutManager& lay_out_manager_;
  };
}
}
#endif

