/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id$
 *
 * Authors:
 *      - initial release
 *
 */
#include "tfs_meta_manager.h"
#include "fsname.h"

using namespace tfs::client;
using namespace tfs::common;
using namespace std;

int TfsMetaManager::initialize(const char* ns_addr)
{
  int ret = TFS_SUCCESS;
  tfs_client_ = TfsClient::Instance();
  if ((ret = tfs_client_->initialize(ns_addr, DEFAULT_BLOCK_CACHE_TIME, 1000, false)) != TFS_SUCCESS)
  {
    TBSYS_LOG(ERROR, "initialize tfs client failed, ns_addr: %s", ns_addr);
  }
  return ret;
}

int32_t TfsMetaManager::get_cluster_id(const char* ns_addr)
{
  return tfs_client_->get_cluster_id(ns_addr);
}

int64_t TfsMetaManager::read_data(const char* ns_addr, const uint32_t block_id, const uint64_t file_id,
    void* buffer, const int32_t offset, const int64_t length)
{
  FSName fsname;
  fsname.set_block_id(block_id);
  fsname.set_file_id(file_id);

  int64_t ret_length = -1;
  int fd = tfs_client_->open(fsname.get_name(), NULL, ns_addr, T_READ);
  if (fd < 0)
  {
    TBSYS_LOG(ERROR, "open read file error, file_name: %s, fd: %d", fsname.get_name(), fd);
  }
  else
  {
    tfs_client_->lseek(fd, offset, T_SEEK_SET);
    ret_length = tfs_client_->read(fd, buffer, length);
    tfs_client_->close(fd);
  }

  return ret_length;
}

int64_t TfsMetaManager::write_data(const char* ns_addr, const void* buffer, const int64_t offset, const int64_t length,
    common::FragMeta& frag_meta)
{
  //TODO get tfs_file from file_pool

  int ret = TFS_SUCCESS;
  int64_t cur_pos = 0;
  int64_t cur_length, left_length = length;
  char tfsname[MAX_FILE_NAME_LEN];
  int fd = tfs_client_->open(NULL, NULL, ns_addr, T_WRITE);
  if (fd < 0)
  {
    TBSYS_LOG(ERROR, "open write file error, fd: %d", fd);
  }
  else
  {
    int64_t write_length = 0;
    do
    {
      cur_length = min(left_length, MAX_WRITE_DATA_IO);
      write_length = tfs_client_->write(fd, reinterpret_cast<const char*>(buffer) + cur_pos, cur_length);
      if (write_length < 0)
      {
        TBSYS_LOG(ERROR, "tfs write data error, ret: %"PRI64_PREFIX"d", write_length);
        ret = TFS_ERROR;
        break;
      }
      cur_pos += write_length;
      left_length -= write_length;
    }
    while(left_length > 0);
    tfs_client_->close(fd, tfsname, MAX_FILE_NAME_LEN);
    TBSYS_LOG(DEBUG, "tfs write success, tfsname: %s", tfsname);

    FSName fsname;
    fsname.set_name(tfsname);

    frag_meta.block_id_ = fsname.get_block_id();
    frag_meta.file_id_ = fsname.get_file_id();
    frag_meta.offset_ = offset;
    frag_meta.size_ = length - left_length;
  }

  return (length - left_length);
}
