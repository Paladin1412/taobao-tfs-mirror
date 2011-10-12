/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: tfs_client_api.cpp 49 2010-11-16 09:58:57Z zongdai@taobao.com $
 *
 * Authors:
 *    daoan<aoan@taobao.com>
 *      - initial release
 *
 */
#include "tfs_rc_client_api.h"
#include "common/define.h"
#include "tfs_rc_client_api_impl.h"

namespace tfs
{
  namespace client
  {
    using namespace tfs::common;
    using namespace std;

    RcClient::RcClient():impl_(NULL)
    {
      impl_ = new RcClientImpl();
    }

    RcClient::~RcClient()
    {
      delete impl_;
      impl_ = NULL;
    }
    TfsRetType RcClient::initialize(const char* str_rc_ip, const char* app_key, const char* str_app_ip,
        const int32_t cache_times, const int32_t cache_items, const char* dev_name, const char* rs_addr)
    {
      int32_t real_cache_times = (cache_times >= 0) ? cache_times : common::DEFAULT_BLOCK_CACHE_TIME;
      int32_t real_cache_items = (cache_items >= 0) ? cache_items : common::DEFAULT_BLOCK_CACHE_ITEMS;
      return impl_->initialize(str_rc_ip, app_key, str_app_ip, real_cache_times, real_cache_items, dev_name, rs_addr);
    }
    TfsRetType RcClient::initialize(const uint64_t rc_ip, const char* app_key, const uint64_t app_ip,
        const int32_t cache_times, const int32_t cache_items, const char* dev_name, const char* rs_addr)
    {
      int32_t real_cache_times = (cache_times >= 0) ? cache_times : common::DEFAULT_BLOCK_CACHE_TIME;
      int32_t real_cache_items = (cache_items >= 0) ? cache_items : common::DEFAULT_BLOCK_CACHE_ITEMS;
      return impl_->initialize(rc_ip, app_key, app_ip, real_cache_times, real_cache_items, dev_name, rs_addr);
    }


    TfsRetType RcClient::logout()
    {
      return impl_->logout();
    }

    void RcClient::set_wait_timeout(const int64_t timeout_ms)
    {
      impl_->set_wait_timeout(timeout_ms);
      return;
    }

    void RcClient::set_log_level(const char* level)
    {
      impl_->set_log_level(level);
    }

    void RcClient::set_log_file(const char* log_file)
    {
      impl_->set_log_file(log_file);
    }

    int RcClient::open(const char* file_name, const char* suffix, const RC_MODE mode,
          const bool large, const char* local_key)
    {
      return impl_->open(file_name, suffix, mode, large, local_key);
    }
    int RcClient::open(const int64_t app_id, const int64_t uid, 
        const char* name, const RcClient::RC_MODE mode)
    {
      return impl_->open(app_id, uid, name, mode);
    }

    TfsRetType RcClient::close(const int fd, char* tfs_name_buff, const int32_t buff_len)
    {
      return impl_->close(fd, tfs_name_buff, buff_len);
    }

    int64_t RcClient::read(const int fd, void* buf, const int64_t count)
    {
      return impl_->read(fd, buf, count);
    }

    int64_t RcClient::readv2(const int fd, void* buf, const int64_t count, TfsFileStat* tfs_stat_buf)
    {
      return impl_->readv2(fd, buf, count, tfs_stat_buf);
    }
    int64_t RcClient::pread(const int fd, void* buf, const int64_t count, const int64_t offset)
    {
      return impl_->pread(fd, buf, count, offset);
    }

    int64_t RcClient::write(const int fd, const void* buf, const int64_t count)
    {
      return impl_->write(fd, buf, count);
    }
    //int64_t RcClient::pwrite(const int fd, const void* buf, const int64_t count, const int64_t offset)
    //{
    //  return impl_->pwrite(fd, buf, count, offset);
    //}

    int64_t RcClient::lseek(const int fd, const int64_t offset, const int whence)
    {
      return impl_->lseek(fd, offset, whence);
    }

    TfsRetType RcClient::fstat(const int fd, common::TfsFileStat* buf)
    {
      return impl_->fstat(fd, buf);
    }

    TfsRetType RcClient::unlink(const char* file_name, const char* suffix, const common::TfsUnlinkType action)
    {
      return impl_->unlink(file_name, suffix, action);
    }

    int64_t RcClient::save_file(const char* local_file, char* tfs_name_buff, const int32_t buff_len,
        const bool is_large_file)
    {
      return impl_->save_file(local_file, tfs_name_buff, buff_len, is_large_file);
    }
    int64_t RcClient::save_file(const char* source_data, const int32_t data_len,
        char* tfs_name_buff, const int32_t buff_len)
    {
      return impl_->save_file(source_data, data_len, tfs_name_buff, buff_len);
    }
    TfsRetType RcClient::create_dir(const int64_t uid, const char* dir_path)
    {
      return impl_->create_dir(uid, dir_path);
    }
    TfsRetType RcClient::rm_dir(const int64_t uid, const char* dir_path)
    {
      return impl_->rm_dir(uid, dir_path);
    }
    TfsRetType RcClient::rm_file(const int64_t uid, const char* file_path)
    {
      return impl_->rm_file(uid, file_path);
    }

    TfsRetType RcClient::mv_dir(const int64_t uid, const char* src_dir_path, const char* dest_dir_path)
    {
      return impl_->mv_dir(uid, src_dir_path, dest_dir_path);
    }
    TfsRetType RcClient::mv_file(const int64_t uid, const char* src_file_path, 
        const char* dest_file_path)
    {
      return impl_->mv_file(uid, src_file_path, dest_file_path);
    }

    TfsRetType RcClient::ls_dir(const int64_t app_id, const int64_t uid, const char* dir_path,
        std::vector<common::FileMetaInfo>& v_file_meta_info)
    {
      return impl_->ls_dir(app_id, uid, dir_path, v_file_meta_info);
    }

    TfsRetType RcClient::ls_file(const int64_t app_id, const int64_t uid,
        const char* file_path,
        common::FileMetaInfo& file_meta_info)
    {
      return impl_->ls_file(app_id, uid, file_path, file_meta_info);
    }
  }
}
