/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Authors:
 *   duolong <duolong@taobao.com>
 *      - initial release
 *   linqing <linqing.zyd@taobao.com>
 *      - modify 2013-07-12
 *
 */
#ifndef TFS_CLIENTV2_TFSSESSION_H_
#define TFS_CLIENTV2_TFSSESSION_H_

#include <Mutex.h>

#include "common/internal.h"
#include "lru.h"

#ifdef WITH_TAIR_CACHE
#include "tair_cache_helper.h"
#endif

namespace tfs
{
  namespace clientv2
  {
    enum CacheHitStatus
    {
      CACHE_HIT_NONE = 0,           // all cache miss
      CACHE_HIT_LOCAL,              // hit local cache
      CACHE_HIT_REMOTE,             // hit tair cache
    };

    struct BlockCache
    {
      time_t last_time_;
      common::VUINT64 ds_;
      common::FamilyInfoExt info_;
    };

    struct File;
    class TfsSession
    {
      public:
        typedef Lru<uint64_t, BlockCache> BLOCK_CACHE_MAP;
        typedef BLOCK_CACHE_MAP::iterator BLOCK_CACHE_MAP_ITER;

      public:
        TfsSession(const std::string& ns_addr_, const int64_t cache_time, const int64_t cache_items);
        virtual ~TfsSession();

        int initialize();
        static void destroy();

        int32_t get_cluster_id() const
        {
          return cluster_id_;
        }

        const std::string& get_ns_addr() const
        {
          return ns_addr_;
        }

        const int64_t get_cache_time() const
        {
          return block_cache_time_;
        }

        const int64_t get_cache_items() const
        {
          return block_cache_items_;
        }

        // global cache control
        int get_block_info(uint64_t& block_id, File& file);
        void expire_block_cache(const uint64_t block_id,
            const CacheHitStatus cache_hit, const int ret);

        // cluser managment function
        int get_cluster_group_count_from_ns();
        int get_cluster_group_seq_from_ns();

      public:
        // local cache management
        void insert_local_block_cache(const uint64_t block_id,
            const common::VUINT64& ds, const common::FamilyInfoExt& info);
        void remove_local_block_cache(const uint64_t block_id);
        bool query_local_block_cache(const uint64_t block_id,
            common::VUINT64& ds, common::FamilyInfoExt& info);

#ifdef WITH_TAIR_CACHE
      private:
        static TairCacheHelper* remote_cache_helper_;
      public:
        int init_remote_cache_helper();
        bool check_remote_cache_init();
        void insert_remote_block_cache(const uint64_t block_id,
            const common::VUINT64& ds, const common::FamilyInfoExt& info);
        void remove_remote_block_cache(const uint64_t block_id);
        bool query_remote_block_cache(const uint64_t block_id,
            common::VUINT64& ds, common::FamilyInfoExt& info);
#endif
      private:
        TfsSession();
        DISALLOW_COPY_AND_ASSIGN(TfsSession);

        // get block info from nameserver
        int get_block_info_ex(const int32_t flag, uint64_t& block_id,
            int32_t& version, common::VUINT64& ds, common::FamilyInfoExt& info);

        // get cluster id from nameserver
        int get_cluster_id_from_ns();

      private:
        tbutil::Mutex mutex_;
        std::string ns_addr_;
        uint64_t ns_id_;
        int32_t cluster_id_;
        const int64_t block_cache_time_;
        const int64_t block_cache_items_;
        BLOCK_CACHE_MAP block_cache_map_;
    };
  }
}
#endif