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
 *   qixiao.zs <qixiao.zs@alibaba-inc.com>
 *      - initial release
 *
 */
#ifndef TFS_COMMON_EXPIRE_DEFINE_H_
#define TFS_COMMON_EXPIRE_DEFINE_H_

#include <vector>
#include <map>
#include <set>
#include <string>
#include "define.h"
#include "kvengine_helper.h"
namespace tfs
{
  namespace common
  {
    class ExpireDefine
    {
      public:
      static const char DELIMITER_EXPIRE;
      static const int32_t HASH_BUCKET_NUM;
      static const int32_t FILE_TYPE_RAW_TFS;
      static const int32_t FILE_TYPE_CUSTOM_TFS;

      /* expire time target key */
      enum {
        EXPTIME_KEY_BUFF = 512,
      };
      static int serialize_name_expiretime_key(const int32_t file_type,
          const std::string& file_name,
          common::KvKey *key, char *data, const int32_t size);

      static int deserialize_name_expiretime_key(const char *data, const int32_t size,
          int8_t* key_type, int32_t* file_type,
          std::string* file_name);

      static int serialize_name_expiretime_value(const int32_t invalid_time,
          common::KvMemValue *value, char *data, const int32_t size);

      static int deserialize_name_expiretime_value(const char *data, const int32_t size,
          int32_t* invalid_time);

      static int serialize_exptime_app_key(const int32_t days_secs, const int32_t hours_secs,
                                const int32_t hash_mod,  const int32_t file_type,
                                const std::string &file_name, common::KvKey *key,
                                char *data, const int32_t size);
      static int deserialize_exptime_app_key(const char *data, const int32_t size,
                                 int32_t* p_days_secs, int32_t* p_hours_secs,
                                 int32_t* p_hash_mod, int32_t *p_file_type,
                                 std::string *file_name);

      static int serialize_es_stat_key(const uint64_t local_ipport, const int32_t num_es,
                                const int32_t task_time, const int32_t hash_bucket_num,
                                const int64_t sum_file_num, common::KvKey *key,
                                char *data, const int32_t size);
      static int transfer_time(const int32_t time, int32_t *p_days_secs, int32_t *p_hours_secs);
    };


    struct OriInvalidTimeValueInfo
    {
      OriInvalidTimeValueInfo();
      int64_t length() const;
      int serialize(char *data, const int64_t data_len, int64_t &pos) const;
      int deserialize(const char *data, const int64_t data_len, int64_t &pos);
      std::string appkey_;
    };

    struct ExpServerBaseInformation
    {
      int deserialize(const char* data, const int64_t data_len, int64_t& pos);
      int serialize(char* data, const int64_t data_len, int64_t& pos) const;
      int64_t length() const;
      uint64_t id_; /** MetaServer id(IP + PORT) **/
      int64_t start_time_; /** MetaServer start time (ms)**/
      int64_t last_update_time_;/** MetaServer last update time (ms)**/
    };
  }
}

#endif
