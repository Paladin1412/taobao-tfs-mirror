/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: sync_backup.cpp 868 2011-09-29 05:07:38Z duanfei@taobao.com $
 *
 * Authors:
 *   duolong <duolong@taobao.com>
 *      - initial release
 *   zongdai <zongdai@taobao.com>
 *      - modify 2010-04-23
 *
 */
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>

#include "tbsys.h"

#include "common/func.h"
#include "common/parameter.h"
#include "common/directory_op.h"
#include "clientv2/fsname.h"
#include "sync_base.h"
#include "sync_backup.h"
#include "dataservice.h"

namespace tfs
{
  namespace dataserver
  {
    using namespace common;
    using namespace clientv2;

    SyncBackup::SyncBackup() : tfs_client_(NULL)
    {
      src_addr_[0] = '\0';
      dest_addr_[0] = '\0';
    }

    SyncBackup::~SyncBackup()
    {
    }

    int SyncBackup::do_sync(const SyncData *sf)
    {
      int ret = TFS_ERROR;
      switch (sf->cmd_)
      {
      case OPLOG_INSERT:
        ret = copy_file(sf->block_id_, sf->file_id_);
        break;
      case OPLOG_REMOVE:
        ret = remove_file(sf->block_id_, sf->file_id_, sf->old_file_id_);
        break;
      case OPLOG_RENAME:
        ret = rename_file(sf->block_id_, sf->file_id_, sf->old_file_id_);
        break;
      }
      return ret;
    }

    int SyncBackup::copy_file(const uint64_t, const uint64_t)
    {
      return TFS_SUCCESS;
    }

    int SyncBackup::remove_file(const uint64_t, const uint64_t, const int32_t)
    {
      return TFS_SUCCESS;
    }

    int SyncBackup::rename_file(const uint64_t, const uint64_t, const uint64_t)
    {
      return TFS_SUCCESS;
    }

    int SyncBackup::remote_copy_file(const uint64_t, const uint64_t)
    {
      return TFS_SUCCESS;
    }

    TfsMirrorBackup::TfsMirrorBackup(SyncBase& sync_base, const char* src_addr, const char* dest_addr):
        sync_base_(sync_base), do_sync_mirror_thread_(0)
    {
      if (NULL != src_addr &&
          strlen(src_addr) > 0 &&
          NULL != dest_addr &&
          strlen(dest_addr) > 0)
      {
        strcpy(src_addr_, src_addr);
        strcpy(dest_addr_, dest_addr);
      }
    }

    TfsMirrorBackup::~TfsMirrorBackup()
    {
    }

    inline BlockManager& TfsMirrorBackup::get_block_manager()
    {
      return dynamic_cast<DataService*>(DataService::instance())->get_block_manager();
    }

    bool TfsMirrorBackup::init()
    {
      bool ret = (strlen(src_addr_) > 0 && strlen(dest_addr_) > 0) ? true : false;
      if (ret)
      {
        tfs_client_ = TfsClientImplV2::Instance();
        ret =
          tfs_client_->initialize(dest_addr_) == TFS_SUCCESS ?
          true : false;
        TBSYS_LOG(INFO, "TfsSyncMirror init. source ns addr: %s, destination ns addr: %s", src_addr_, dest_addr_);
        if (do_sync_mirror_thread_ == 0)
          do_sync_mirror_thread_ = new DoSyncMirrorThreadHelper(sync_base_);
      }
      return ret;
    }

    void TfsMirrorBackup::destroy()
    {
      if (0 != do_sync_mirror_thread_)
      {
        do_sync_mirror_thread_->join();
        do_sync_mirror_thread_ = 0;
      }
    }

    int TfsMirrorBackup::do_sync(const SyncData *sf)
    {
      int ret = TFS_ERROR;
      switch (sf->cmd_)
      {
      case OPLOG_INSERT:
        ret = copy_file(sf->block_id_, sf->file_id_);
        break;
      case OPLOG_REMOVE:
        ret = remove_file(sf->block_id_, sf->file_id_, static_cast<TfsUnlinkType>(sf->old_file_id_));
        break;
      }

      for (int i = 0; i < SYSPARAM_DATASERVER.max_sync_retry_count_ && TFS_SUCCESS != ret; i++)
      {
        if (i > 0)
        {
          int wait_time = i * i;
          if (wait_time > SYSPARAM_DATASERVER.max_sync_retry_interval_)
          {
            wait_time = SYSPARAM_DATASERVER.max_sync_retry_interval_;
          }
          sleep(wait_time);
        }
        FSName fsname(sf->block_id_, sf->file_id_);
        ret = sync_stat(sf->block_id_, sf->file_id_);
        TBSYS_LOG(INFO, "sync_stat %s block_id: %"PRI64_PREFIX"u, file_id: %"PRI64_PREFIX"u, action: %d, ret: %d, retry_count: %d",
          fsname.get_name(), sf->block_id_, sf->file_id_, sf->cmd_, ret, i + 1);
      }

      return ret;
    }

    int TfsMirrorBackup::remote_copy_file(const uint64_t block_id, const uint64_t file_id)
    {
      FSName fsname(block_id, file_id);
      int32_t ret = (INVALID_BLOCK_ID == block_id) ? EXIT_BLOCKID_ZERO_ERROR : TFS_SUCCESS;
      if (TFS_SUCCESS == ret)
      {
        int dest_fd = -1;
        int32_t src_fd = tfs_client_->open(block_id, file_id, src_addr_, T_READ | T_FORCE);
        if (src_fd < 0)
        {
          // if the block is missing, need not sync
          ret = file_not_exist(ret) ? TFS_SUCCESS : src_fd;
        }
        else // open src file success
        {
          ret = tfs_client_->set_option_flag(src_fd, FORCE_STAT);
          if (TFS_SUCCESS == ret)
          {
            TfsFileStat file_stat;
            ret = tfs_client_->fstat(src_fd, &file_stat);
            if (TFS_SUCCESS != ret) // src file stat fail
            {
              // if block not found or the file is deleted, need not sync
              if (file_not_exist(ret))
              {
                TBSYS_LOG(WARN, "%s stat src file fail. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u, ret: %d",
                          fsname.get_name(), block_id, file_id, ret);
              }
            }
            else // src file stat ok
            {
              ret = file_stat.size_ <= 0 ? EXIT_READ_FILE_SIZE_ERROR: TFS_SUCCESS;
              if (TFS_SUCCESS == ret)
              {
                dest_fd = tfs_client_->open(block_id, file_id, dest_addr_, T_WRITE|T_NEWBLK);
                ret = dest_fd < 0 ? dest_fd : TFS_SUCCESS;
                if (TFS_SUCCESS != ret)
                {
                  // maybe ignored if not reset error type
                  TBSYS_LOG(WARN, "%s open dest write fail. blockid: %"PRI64_PREFIX"u, fileid: %" PRI64_PREFIX "u, ret: %d",
                          fsname.get_name(), block_id, file_id, dest_fd);
                }
                else // source file stat ok, destination file open ok
                {
                  tfs_client_->set_option_flag(dest_fd, TFS_FILE_NO_SYNC_LOG);
                  if (TFS_SUCCESS == ret)
                  {
                    uint32_t crc = 0;
                    char data[MAX_READ_SIZE];
                    int32_t total_length = 0, length = 0, write_length  = 0;
                    while (total_length < file_stat.size_
                          && TFS_SUCCESS == ret)
                    {
                      length = tfs_client_->read(src_fd, data, MAX_READ_SIZE);
                      ret = length <= 0 ? EXIT_READ_FILE_ERROR: TFS_SUCCESS;
                      if (TFS_SUCCESS != ret)
                      {
                        TBSYS_LOG(WARN, "%s read src file fail. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u, length: %d, ret: %d", fsname.get_name(), block_id, file_id, length, ret);
                      }
                      else // read src file success
                      {
                        write_length = tfs_client_->write(dest_fd, data, length);
                        ret = write_length != length ? EXIT_WRITE_FILE_ERROR : TFS_SUCCESS;
                        if (TFS_SUCCESS != ret)
                        {
                          TBSYS_LOG(WARN, "%s write dest file fail. blockid: %"PRI64_PREFIX"u, fileid: %" PRI64_PREFIX "u, length: %d, write_length: %d",
                              fsname.get_name(), block_id, file_id, length, write_length);
                        }
                        else
                        {
                          crc = Func::crc(crc, data, length);
                          total_length += length;
                        }
                      } // read src file success
                    } // while (total_length < file_stat.st_size && TFS_SUCCESS == ret)

                    // write successful & check file size & check crc
                    if (TFS_SUCCESS == ret)
                    {
                      ret = total_length == file_stat.size_ ? TFS_SUCCESS : EXIT_SYNC_FILE_ERROR;//check file size
                      if (TFS_SUCCESS != ret)
                      {
                        TBSYS_LOG(WARN, "file size error. %s, blockid: %"PRI64_PREFIX"u, fileid :%" PRI64_PREFIX "u, crc: %u <> %u, size: %d <> %"PRI64_PREFIX"d",
                            fsname.get_name(), block_id, file_id, crc, file_stat.crc_, total_length, file_stat.size_);
                      }
                      else
                      {
                        ret = crc != file_stat.crc_ ? EXIT_CHECK_CRC_ERROR : TFS_SUCCESS;//check crc
                        if (TFS_SUCCESS != ret)
                        {
                          TBSYS_LOG(WARN, "crc error. %s, blockid: %"PRI64_PREFIX"u, fileid :%" PRI64_PREFIX "u, crc: %u <> %u, size: %d <> %"PRI64_PREFIX"d",
                              fsname.get_name(), block_id, file_id, crc, file_stat.crc_, total_length, file_stat.size_);
                        }
                      }
                    } // write successful & check file size & check crc
                  } // source file stat ok, destination file open ok, size normal
                } // source file stat ok, destination file open ok
              }
            } // source file stat ok
          }//set option flag ok
        } // src file open ok

        if (src_fd > 0)
        {
          // close source file
          tfs_client_->close(src_fd);
        }
        if (dest_fd > 0)
        {
          // close destination file anyway
          int tmp_ret = tfs_client_->close(dest_fd);
          if (tmp_ret != TFS_SUCCESS)
          {
            TBSYS_LOG(WARN, "%s close dest tfsfile fail. blockid: %"PRI64_PREFIX"u, fileid: %" PRI64_PREFIX "u. ret: %d",
                fsname.get_name(), block_id, file_id, tmp_ret);
            ret = tmp_ret;
          }
        }
      }
      if (TFS_SUCCESS != ret)
      {
        ret = (file_not_exist(ret)) ? TFS_SUCCESS : ret;
      }
      TBSYS_LOG(DEBUG, "tfs remote copy file %s %s to dest: %s. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u, ret: %d",
           fsname.get_name(), TFS_SUCCESS == ret ? "successful" : "fail", dest_addr_, block_id, file_id, ret);
      return ret;
    }

    int TfsMirrorBackup::copy_file(const uint64_t block_id, const uint64_t file_id)
    {
      FSName fsname(block_id, file_id);
      int32_t ret = common::INVALID_BLOCK_ID != block_id ? TFS_SUCCESS : EXIT_BLOCKID_ZERO_ERROR;
      if (TFS_SUCCESS == ret)
      {
        ret = get_block_manager().exist(block_id) ? TFS_SUCCESS : EXIT_NO_LOGICBLOCK_ERROR;
        if (TFS_SUCCESS != ret)
        {
          ret = remote_copy_file(block_id, file_id);
        }
        else
        {
          FileInfoV2 finfo;
          finfo.id_ = file_id;
          ret = get_block_manager().stat(finfo, READ_DATA_OPTION_FLAG_FORCE, block_id, block_id);
          if (TFS_SUCCESS != ret)
          {
            TBSYS_LOG(WARN, "stat file %s fail. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u,ret: %d",
                  fsname.get_name(), block_id, file_id, ret);
          }
          else
          {
            uint32_t crc = 0;
            char data[MAX_READ_SIZE];
            finfo.size_ -= sizeof(FileInfoInDiskExt);
            int32_t offset = sizeof(FileInfoInDiskExt), write_length = 0, length = MAX_READ_SIZE, total_length = 0, dest_fd = -1, result = 0;
            while (total_length < finfo.size_ && TFS_SUCCESS == ret)
            {
              length = ((finfo.size_ - total_length) > MAX_READ_SIZE) ? MAX_READ_SIZE : finfo.size_ - total_length;
              result = get_block_manager().read(data, length, offset, file_id, READ_DATA_OPTION_FLAG_FORCE, block_id, block_id);
              ret = result == length ? TFS_SUCCESS : EXIT_READ_FILE_SIZE_ERROR;
              if (TFS_SUCCESS != ret)
              {
                  TBSYS_LOG(WARN, "read file %s fail. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u, offset: %d, length %d <> %d, ret: %d",
                        fsname.get_name(), block_id, file_id, offset, length,result,ret);
              }
              else
              {
                if (-1 == dest_fd)
                {
                  dest_fd = tfs_client_->open(block_id, file_id, dest_addr_, T_WRITE|T_NEWBLK);
                  ret = (dest_fd < 0) ? dest_fd : TFS_SUCCESS;
                  if (TFS_SUCCESS != ret)
                  {
                    TBSYS_LOG(WARN, "open dest file %s fail. block_id: %"PRI64_PREFIX"u, file_id: %"PRI64_PREFIX"u ,ret: %d",
                      fsname.get_name(), block_id, file_id, ret);
                  }
                  else
                  {
                    //set no sync flag avoid the data being sync in the backup cluster again
                    ret = tfs_client_->set_option_flag(dest_fd, TFS_FILE_NO_SYNC_LOG);
                  }
                }
                ret = (dest_fd < 0) ? dest_fd : TFS_SUCCESS;
                if (TFS_SUCCESS == ret)
                {
                  write_length = tfs_client_->write(dest_fd, data, length);
                  ret = write_length != length ? EXIT_WRITE_FILE_ERROR : TFS_SUCCESS;
                  if (TFS_SUCCESS != ret)
                  {
                    TBSYS_LOG(WARN,"write dest file %s fail. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u, length: %d <> %d, size: %d",
                        fsname.get_name(), block_id, file_id, write_length, length, finfo.size_);
                  }
                  else
                  {
                    offset += length;
                    total_length += length;
                    crc = Func::crc(crc, data, length);
                  }
                }
              }
            }

            //write successful & check file size & check crc
            if (TFS_SUCCESS == ret)
            {
              ret = total_length == finfo.size_ ? TFS_SUCCESS : EXIT_SYNC_FILE_ERROR; // check file size
              if (TFS_SUCCESS != ret)
              {
                TBSYS_LOG(WARN, "file size error. %s, blockid: %"PRI64_PREFIX"u, fileid :%" PRI64_PREFIX "u, crc: %u <> %u, size: %d <> %d",
                  fsname.get_name(), block_id, file_id, crc, finfo.crc_, total_length, finfo.size_);
              }
            }
            if (TFS_SUCCESS == ret)
            {
              ret = crc != finfo.crc_ ? EXIT_CHECK_CRC_ERROR : TFS_SUCCESS; // check crc
              if (TFS_SUCCESS != ret)
              {
                Func::hex_dump(data, 8, true, TBSYS_LOG_LEVEL_DEBUG);
                TBSYS_LOG(WARN, "file crc error. %s, blockid: %"PRI64_PREFIX"u, fileid :%" PRI64_PREFIX "u, crc: %u <> %u, size: %d <> %d",
                  fsname.get_name(), block_id, file_id, crc, finfo.crc_, total_length, finfo.size_);
              }
            }
            // close destination tfsfile anyway
            if (dest_fd > 0)
            {
              int32_t rt = tfs_client_->close(dest_fd);
              if (TFS_SUCCESS != rt)
              {
                TBSYS_LOG(WARN, "close dest file fail.%s blockid: %"PRI64_PREFIX"u, fileid :%" PRI64_PREFIX "u, rt: %d",
                         fsname.get_name(), block_id, file_id, rt);
              }
            }
          }
        }
      }
      if (TFS_SUCCESS != ret)
      {
        ret = (file_not_exist(ret)) ? TFS_SUCCESS : ret;
      }
      TBSYS_LOG(DEBUG, "tfs mirror copy file %s %s to dest: %s. blockid: %"PRI64_PREFIX"u, fileid: %"PRI64_PREFIX"u, ret: %d",
           fsname.get_name(), TFS_SUCCESS == ret ? "successful" : "fail", dest_addr_, block_id, file_id, ret);
      return ret;
    }

    int TfsMirrorBackup::remove_file(const uint64_t block_id, const uint64_t file_id,
                                     const TfsUnlinkType action)
    {
      FSName fsname(block_id, file_id);
      int32_t ret = common::INVALID_BLOCK_ID != block_id && common::INVALID_FILE_ID != file_id ? TFS_SUCCESS : EXIT_BLOCKID_ZERO_ERROR;
      if (TFS_SUCCESS == ret)
      {
        int32_t dest_fd = tfs_client_->open(block_id, file_id, dest_addr_, T_WRITE|T_NEWBLK);
        ret = dest_fd > 0 ? TFS_SUCCESS : TFS_ERROR;
        if (TFS_SUCCESS != ret)
        {
          TBSYS_LOG(INFO, "tfs mirror remove file fail, open dest file fail %s, ret: %d, block_id: %"PRI64_PREFIX"u, file_id: %"PRI64_PREFIX"u ,dest addr: %s, action: %d",
            fsname.get_name(), ret, block_id, file_id, dest_addr_, action);
        }
        else
        {
          ret = tfs_client_->set_option_flag(dest_fd, TFS_FILE_NO_SYNC_LOG);
        }
        if (TFS_SUCCESS == ret)
        {
          int64_t file_size = 0;
          ret = tfs_client_->unlink(file_size, dest_fd, action);
        }
      }

      TBSYS_LOG(INFO, "tfs mirror remove file %s %s, ret: %d, block_id: %"PRI64_PREFIX"u, file_id: %"PRI64_PREFIX"u ,dest addr: %s, action: %d",
          fsname.get_name(), (TFS_SUCCESS == ret) ? "successful" : "fail", ret, block_id, file_id, dest_addr_, action);
      return ret;
    }

    void TfsMirrorBackup::DoSyncMirrorThreadHelper::run()
    {
      sync_base_.run_sync_mirror();
    }

    int TfsMirrorBackup::get_file_info(const char* nsip, const uint64_t block_id, const uint64_t file_id, TfsFileStat& buf)
    {
      FSName fsname(block_id, file_id, tfs_client_->get_cluster_id());
      int32_t fd = tfs_client_->open(block_id, file_id, nsip, T_READ | T_FORCE);
      int32_t ret = fd < 0 ? fd : TFS_SUCCESS;
      if (TFS_SUCCESS != ret)
      {
        TBSYS_LOG(WARN, "open file %s failed, ret: %d, block_id: %"PRI64_PREFIX"u, file_id: %"PRI64_PREFIX"u", fsname.get_name(),ret, block_id, file_id);
      }
      else
      {
        ret = tfs_client_->set_option_flag(fd, FORCE_STAT);
        if (TFS_SUCCESS == ret)
        {
          ret = tfs_client_->fstat(fd, &buf);
          if (TFS_SUCCESS != ret)
          {
            TBSYS_LOG(WARN, "stat file %s failed, ret: %d, block_id: %"PRI64_PREFIX"u, file_id: %"PRI64_PREFIX"u, addr: %s", fsname.get_name(),ret, block_id, file_id, nsip);
          }
        }
        tfs_client_->close(fd);
      }
      return ret;
    }

    bool TfsMirrorBackup::file_not_exist(int ret)
    {
      return ((EXIT_BLOCK_NOT_FOUND == ret) ||  // ns cannot find block
         (EXIT_NO_LOGICBLOCK_ERROR == ret) ||  // ds cannot find logic block
         (EXIT_META_NOT_FOUND_ERROR == ret) ||
         (EXIT_NO_BLOCK == ret));// ds cannot find file in index
    }

    int TfsMirrorBackup::sync_stat(const uint64_t block_id, const uint64_t file_id)
    {
      int ret = TFS_SUCCESS;

      FSName fsname(block_id, file_id);
      TfsFileStat src_stat;
      TfsFileStat dest_stat;
      ret = get_file_info(src_addr_, block_id, file_id, src_stat);
      if (file_not_exist(ret))   // not exsit in source, just ignore
      {
        ret = TFS_SUCCESS;
      }
      else if (TFS_SUCCESS == ret)
      {
        dest_stat.flag_ = 0;
        ret = get_file_info(dest_addr_, block_id, file_id, dest_stat);
        if (file_not_exist(ret)) // not exsit in dest
        {
          ret = copy_file(block_id, file_id);
        }

        if (TFS_SUCCESS == ret)  // just sync stat;
        {
          if (src_stat.flag_ != dest_stat.flag_)
          {
            int action = (src_stat.flag_ << 4);  // sync flag
            ret = remove_file(block_id, file_id, static_cast<common::TfsUnlinkType>(action));
          }
        }
      }
      return ret;
    }
  }
}
