/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: tfs_file.cpp 19 2010-10-18 05:48:09Z nayan@taobao.com $
 *
 * Authors:
 *   duolong <duolong@taobao.com>
 *      - initial release
 *
 */
#include "tfs_small_file.h"

using namespace tfs::client;
using namespace tfs::common;

TfsSmallFile::TfsSmallFile()
{
}

TfsSmallFile::~TfsSmallFile()
{
}

int TfsSmallFile::open(const char *file_name, const char *suffix, const int flags, ... )
{
  return open_ex(file_name, suffix, flags);
}

int64_t TfsSmallFile::read(void* buf, const int64_t count)
{
  return read_ex(buf, count, offset_);
}

int64_t TfsSmallFile::write(const void* buf, const int64_t count)
{
  return write_ex(buf, count, offset_);
}

int64_t TfsSmallFile::lseek(const int64_t offset, const int whence)
{
  return lseek_ex(offset, whence);
}

int64_t TfsSmallFile::pread(void *buf, const int64_t count, const int64_t offset)
{
  return pread_ex(buf, count, offset);
}

int64_t TfsSmallFile::pwrite(const void *buf, const int64_t count, const int64_t offset)
{
  return pwrite_ex(buf, count, offset);
}

int TfsSmallFile::fstat(TfsFileStat* file_stat, const TfsStatType mode)
{
  int ret = TFS_ERROR;
  if (file_stat != NULL)
  {
    FileInfo file_info;
    ret = fstat_ex(&file_info, mode);
    if (TFS_SUCCESS == ret)
    {
      file_stat->file_id_ = file_info.id_;
      file_stat->offset_ = file_info.offset_;
      file_stat->size_ = file_info.size_;
      file_stat->usize_ = file_info.usize_;
      file_stat->modify_time_ = file_info.modify_time_;
      file_stat->create_time_ = file_info.create_time_;
      file_stat->flag_ = file_info.flag_;
      file_stat->crc_ = file_info.crc_;
    }
  }
  return ret;
}

int TfsSmallFile::close()
{
  return close_ex();
}

int64_t TfsSmallFile::get_file_length()
{
  int64_t ret_len = 0;
  FileInfo file_info;
  int ret = fstat_ex(&file_info, NORMAL_STAT);
  if (TFS_SUCCESS == ret)
  {
    ret_len = file_info.size_;
  }
  return ret_len;
}

int TfsSmallFile::unlink(const char* file_name, const char* suffix, const TfsUnlinkType action)
{
  int ret = open_ex(file_name, suffix, T_WRITE | T_NOLEASE);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "tfs unlink fail, file_name: %s, action: %d", (file_name == NULL) ? file_name : "NULL", action);
  }
  else
  {
    meta_seg_->file_number_ = action;
    ret = unlink_process();
  }
  return ret;
}

int64_t TfsSmallFile::get_segment_for_read(const int64_t offset, char* buf, const int64_t count)
{
  return get_meta_segment(offset, buf,
                          count > ClientConfig::segment_size_ ? ClientConfig::segment_size_ : count);
}

int64_t TfsSmallFile::get_segment_for_write(const int64_t offset, const char* buf, const int64_t count)
{
  return get_meta_segment(offset, buf,
                          count > ClientConfig::segment_size_ ? ClientConfig::segment_size_ : count);
}

int TfsSmallFile::read_process(int64_t& read_size)
{
  int ret = TFS_SUCCESS;
  //set status
  processing_seg_list_[0]->status_ = SEG_STATUS_OPEN_OVER;
  processing_seg_list_[0]->pri_ds_index_ = processing_seg_list_[0]->seg_info_.file_id_ %
    processing_seg_list_[0]->ds_.size();

  if ((ret = process(FILE_PHASE_READ_FILE)) != TFS_SUCCESS)
  {
    TBSYS_LOG(ERROR, "read data fail, ret: %d", ret);
  }
  finish_read_process(ret, read_size);
  return ret;
}

int TfsSmallFile::write_process()
{
  int ret = TFS_SUCCESS;
  // just retry this block
  processing_seg_list_[0]->status_ = SEG_STATUS_CREATE_OVER;
  // write data
  if ((ret = process(FILE_PHASE_WRITE_DATA)) != TFS_SUCCESS)
  {
    TBSYS_LOG(ERROR, "write data fail, ret: %d", ret);
  }
  return ret;
}

int32_t TfsSmallFile::finish_write_process(const int status)
{
  int32_t count = 0;
  SEG_DATA_LIST_ITER it = processing_seg_list_.begin();

  if (TFS_SUCCESS == status)
  {
    count = processing_seg_list_.size();
    for (; it != processing_seg_list_.end(); it++)
    {
      if ((*it)->delete_flag_)
      {
        tbsys::gDelete(*it);
      }
    }
    processing_seg_list_.clear();
  }
  else
  {
    while (it != processing_seg_list_.end())
    {
      // for small file, segment just write, not close
      if (SEG_STATUS_BEFORE_CLOSE_OVER == (*it)->status_)
      {
        if ((*it)->delete_flag_)
        {
          tbsys::gDelete(*it);
        }
        it = processing_seg_list_.erase(it);
        count++;
      }
      else
      {
        ++it;
      }
    }
  }

  return count;
}

int TfsSmallFile::close_process()
{
  int ret = TFS_SUCCESS;
  get_meta_segment(0, NULL, 0);
  if ((ret = process(FILE_PHASE_CLOSE_FILE)) != TFS_SUCCESS)
  {
    TBSYS_LOG(ERROR, "close tfs file fail, ret: %d", ret);
  }
  return ret;
}

int TfsSmallFile::unlink_process()
{
  int ret = TFS_SUCCESS;
  get_meta_segment(0, NULL, 0);
  if ((ret = process(FILE_PHASE_UNLINK_FILE)) != TFS_SUCCESS)
  {
    TBSYS_LOG(ERROR, "unlink file fail, ret: %d", ret);
  }
  return ret;
}
