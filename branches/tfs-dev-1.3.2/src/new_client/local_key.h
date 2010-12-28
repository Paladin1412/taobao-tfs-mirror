#ifndef TFS_CLIENT_LOCALKEY_H_
#define TFS_CLIENT_LOCALKEY_H_

#include <tbsys.h>
#include <Memory.hpp>
#include "common/file_op.h"
#include "common/interval.h"

namespace tfs
{
  namespace client
  {
    enum SegmentStatus
    {
      SEG_STATUS_UNINIT = 0,
      SEG_STATUS_SUCCESS,
      SEG_STATUS_FAIL
    };

    enum TfsFileEofFlag
    {
      TFS_FILE_EOF_FLAG_NO = 0x00,
      TFS_FILE_EOF_FLAG_YES
    };

#pragma pack(4)
    struct SegmentHead
    {
      int32_t count_;           // segment count
      int64_t size_;            // total size that segments contain
    };

    struct SegmentInfo
    {
      uint32_t block_id_;       // block id
      uint64_t file_id_;        // file id
      int64_t offset_;          // offset in current file
      int32_t size_;            // size of segment
      int32_t crc_;             // crc checksum of segment

      SegmentInfo()
      {
        memset(this, 0, sizeof(*this));
      }
      bool operator < (const SegmentInfo& si) const
      {
        return offset_ < si.offset_;
      }
    };
#pragma pack()

    struct SegmentData
    {
      SegmentInfo seg_info_;
      char* buf_;                   // buffer start
      common::FileInfo* file_info_;
      int64_t cur_offset_;
      int32_t cur_size_;
      uint64_t file_number_;
      common::VUINT64 ds_;
      int32_t pri_ds_index_;
      int32_t last_elect_ds_id_;
      int32_t status_;
      TfsFileEofFlag eof_;

      SegmentData() : buf_(NULL), file_info_(NULL), cur_offset_(0), cur_size_(0), file_number_(0), pri_ds_index_(-1),
                      last_elect_ds_id_(0), status_(SEG_STATUS_UNINIT), eof_(TFS_FILE_EOF_FLAG_NO)
      {
      }

      SegmentData(SegmentData& seg_data)
      {
        memcpy(&seg_info_, &seg_data.seg_info_, sizeof(seg_info_));
        buf_ = seg_data.buf_;
        file_info_ = seg_data.file_info_;   // copy ?
        cur_offset_ = seg_data.cur_offset_;
        cur_size_ = seg_data.cur_size_;
        file_number_ = seg_data.file_number_;
        ds_ = seg_data.ds_;
        pri_ds_index_ = seg_data.pri_ds_index_;
        last_elect_ds_id_ = seg_data.last_elect_ds_id_;
        status_ = seg_data.status_;
        eof_ = seg_data.eof_;
      }

      ~SegmentData()
      {
        tbsys::gDelete(file_info_);
      }
    };

    typedef std::vector<SegmentData*> SEG_DATA_LIST;
    typedef std::vector<SegmentData*>::iterator SEG_DATA_LIST_ITER;

    class LocalKey
    {
    public:
      typedef std::set<SegmentInfo> SEG_SET;
      typedef std::set<SegmentInfo>::iterator SEG_SET_ITER;

      LocalKey();
      //LocalKey(const char* local_key, const uint64_t addr);
      ~LocalKey();

      int initialize(const char* local_key, const uint64_t addr);

      int load();
      int load(const char* buf);
      int save();
      int remove();

      int get_segment_for_write(const int64_t offset, const char* buf,
                                int64_t size, SEG_DATA_LIST& seg_list);
      int get_segment_for_read(const int64_t offset, const char* buf,
                               const int64_t size, SEG_DATA_LIST& seg_list);

      int add_segment(SegmentInfo& seg_info);

      int32_t get_data_size();  // get segment data size
      int64_t get_file_size();  // get size that segments contain
      int dump_data(char* buf);

    private:
      void destroy_info();
      int load_head(const char* buf);
      int load_segment(const char* buf);
      void get_segment(const int64_t start, const int64_t end,
                      const char* buf, int64_t& size, SEG_DATA_LIST& seg_list);

    private:
      SegmentHead seg_head_;
      common::FileOperation* file_op_;
      SEG_SET seg_info_;
    };
  }
}

#endif  // TFS_CLIENT_LOCALKEY_H_
