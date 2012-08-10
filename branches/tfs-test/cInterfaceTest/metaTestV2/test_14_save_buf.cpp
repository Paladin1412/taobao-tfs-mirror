/*
* (C) 2007-2011 Alibaba Group Holding Limited.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License version 2 as
* published by the Free Software Foundation.
*
*
* Version: $Id
*
* Authors:
*   xueya.yy <xueya.yy@taobao.com>
*      - initial release
*
*/

#include "tfs_client_impl_init.h"

TEST_F(TfsInit, 01_save_buffer_byte_right)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 100*(1<<10);
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(data_len, Ret);
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 02_save_buffer_byte_less)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 50*(1<<10);
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(data_len, Ret);
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 03_save_buffer_byte_zero)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 0;
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(0, Ret);
  //����ط�����Ҫɾ�������һ����¼
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 04_save_buffer_wrong_length)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = -1;
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_GT(0, Ret);
 //���ʱ���create_file����û��д��
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 05_save_buffer_byte_more)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 100*(1<<10) + 1;
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
 //����ֵ��Ȼ��102401���������ݿ�����������¼,�൱�ڴ�ɹ��ˣ�
  EXPECT_EQ(data_len, Ret);
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 06_save_buffer_byte_empty)
{
  char tfs_name[19] = "/test";
  char buf[10] = "";
  int64_t Ret = -1;
  int32_t data_len = 10;

  // ���ļ����Դ��ȥ��ò��ԭ���Ŀ��ļ��ǲ��еģ����ﷵ��ֵ��10
  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(data_len, Ret);
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 07_save_buffer_byte_null)
{
  char tfs_name[19] = "/test";
  int64_t Ret = -1;
  int32_t data_len = 100*(1<<10);
  const char* buf = NULL;

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_GT(0, Ret);
  //���ʱ��Ҳ�ᴴ���ļ�����û��д������
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 08_save_buffer_null_tfs_file)
{
  const char* tfs_name = NULL;
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 100*(1<<10);
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_GT(0, Ret);
}

TEST_F(TfsInit, 09_save_buffer_with_same_dir)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 100*(1<<10);
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->create_dir(uid, tfs_name);
  EXPECT_EQ(0, Ret);
  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(100*(1<<10), Ret);
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
  Ret = tfsclient->rm_dir(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 10_save_file_many_times)
{
  char tfs_name[19] = "/test";
  char buf[100*(1<<10)];
  int64_t Ret = -1;
  int32_t data_len = 100*(1<<10);
  data_in(a100K, buf, 0, 100*(1<<10));

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(100*(1<<10), Ret);
  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_GT(0, Ret);
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

TEST_F(TfsInit, 11_save_buffer_large_bytes)
{
  char tfs_name[19] = "/test";
  char* buf = new char[6*(1<<20)];  // max is 8M
  memset(buf, 0, 6*(1<<20)*sizeof(char));
  int64_t Ret = -1;
  int32_t data_len = 6*(1<<20);

  Ret = tfsclient->save_buf(uid, buf, data_len, tfs_name);
  EXPECT_EQ(data_len, Ret);
  delete[] buf;
  Ret = tfsclient->rm_file(uid, tfs_name);
  EXPECT_EQ(0, Ret);
}

int main(int argc, char* argv[])
{
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
