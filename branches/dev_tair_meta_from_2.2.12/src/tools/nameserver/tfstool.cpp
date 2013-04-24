/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: tfstool.cpp 1000 2011-11-03 02:40:09Z mingyan.zc@taobao.com $
 *
 * Authors:
 *   duolong <duolong@taobao.com>
 *      - initial release
 *   chuyu <chuyu@taobao.com>
 *      - modify 2010-03-20
 *
 */
#include <stdio.h>
#include <pthread.h>
#include <signal.h>

#include <vector>
#include <string>
#include <map>

#include "tbsys.h"

#include "common/internal.h"
#include "common/config_item.h"
#include "common/client_manager.h"
#include "common/status_message.h"
#include "common/meta_server_define.h"
#include "common/kv_meta_define.h"
#include "common/kv_rts_define.h"
#include "message/server_status_message.h"
#include "message/client_cmd_message.h"
#include "message/message_factory.h"
#include "message/kv_rts_message.h"
#include "common/base_packet_streamer.h"
#include "tools/util/tool_util.h"
#include "tools/util/ds_lib.h"
#include "new_client/fsname.h"
#include "new_client/tfs_client_impl.h"
#include "new_client/tfs_rc_client_api_impl.h"
#include "new_client/tfs_meta_client_api_impl.h"


using namespace std;
using namespace tfs::client;
using namespace tfs::common;
using namespace tfs::message;
using namespace tfs::tools;

static TfsClientImpl* g_tfs_client = NULL;
static STR_FUNC_MAP g_cmd_map;
static int64_t app_id = 1;
static int64_t uid = 1234;
static const char* dev_name = "bond0";
static const char* app_ip = tbsys::CNetUtil::addrToString(static_cast<uint64_t>(tbsys::CNetUtil::getLocalAddr(dev_name))).c_str();
static const char* default_app_key = "tfscom";
//static const char* default_app_key = "tfsNginxA01";
char app_key[256];
//NameMetaClientImpl impl;

typedef enum {
  META_RAW = 0,
  META_NAME,
  META_KV,
} MetaType;

#ifdef _WITH_READ_LINE
#include "readline/readline.h"
#include "readline/history.h"

char* match_cmd(const char* text, int state)
{
  static STR_FUNC_MAP_ITER it;
  static int len = 0;
  const char* cmd = NULL;

  if (!state)
  {
    it = g_cmd_map.begin();
    len = strlen(text);
  }

  while(it != g_cmd_map.end())
  {
    cmd = it->first.c_str();
    it++;
    if (strncmp(cmd, text, len) == 0)
    {
      int32_t cmd_len = strlen(cmd) + 1;
      // memory will be freed by readline
      return strncpy(new char[cmd_len], cmd, cmd_len);
    }
  }
  return NULL;
}

char** tfscmd_completion (const char* text, int start, int)
{
  rl_attempted_completion_over = 1;
  // at the start of line, then it's a cmd completion
  return (0 == start) ? rl_completion_matches(text, match_cmd) : (char**)NULL;
}
#endif

static void sign_handler(const int32_t sig);
static void usage(const char* name);
void init();
int main_loop();
int do_cmd(char* buffer);

int put_file_ex(const VSTRING& param, const bool unique, const bool is_large = false);
int put_file_raw_ex(const VSTRING& param, const bool is_large = false);
int remove_file_ex(const VSTRING& param, const int32_t unique);
int remove_file_raw_ex(const VSTRING& param, TfsUnlinkType type);

int cmd_cd(const VSTRING& param);
int cmd_ls(const VSTRING& param);
int cmd_pwd(const VSTRING& param);
int cmd_show_help(const VSTRING& param);
int cmd_quit(const VSTRING& param);
int cmd_put_file(const VSTRING& param);
int cmd_uput_file(const VSTRING& param);
int cmd_put_large_file(const VSTRING& param);
int cmd_get_file(const VSTRING& param);
int cmd_remove_file(const VSTRING& param);
int cmd_uremove_file(const VSTRING& param);
int cmd_undel_file(const VSTRING& param);
int cmd_hide_file(const VSTRING& param);
int cmd_rename_file(const VSTRING& param);
int cmd_stat_file(const VSTRING& param);
int cmd_stat_blk(const VSTRING& param);
int cmd_visit_count_blk(const VSTRING& param);
int cmd_list_file_info(const VSTRING& param);
int cmd_batch_file(const VSTRING& param);
int cmd_check_file_info(const VSTRING& param);
int cmd_list_block(const VSTRING& param);

//for name meta server

//the function of raw tfs
int cmd_put_file_raw(const VSTRING& param);
int cmd_put_large_file_raw(const VSTRING& param);
int cmd_get_file_raw(const VSTRING& param);
int cmd_stat_file_raw(const VSTRING& param);
int cmd_remove_file_raw(const VSTRING& param);
int cmd_unremove_file_raw(const VSTRING& param);
int cmd_hide_file_raw(const VSTRING& param);

int cmd_stat_file_meta(const VSTRING& param);
int cmd_ls_dir_meta(const VSTRING& param);
int cmd_ls_file_meta(const VSTRING& param);
int cmd_create_dir_meta(const VSTRING& param);
int cmd_create_file_meta(const VSTRING& param);
int cmd_rm_dir_meta(const VSTRING& param);
int cmd_rm_file_meta(const VSTRING& param);
int cmd_put_file_meta(const VSTRING& param);
int cmd_get_file_meta(const VSTRING& param);
int cmd_is_dir_exist_meta(const VSTRING& param);
int cmd_is_file_exist_meta(const VSTRING& param);

// for kv meta server
int cmd_put_bucket(const VSTRING& param);
int cmd_get_bucket(const VSTRING& param);
int cmd_del_bucket(const VSTRING& param);
int cmd_head_bucket(const VSTRING& param);
int cmd_put_bucket_tag(const VSTRING &param);
int cmd_get_bucket_tag(const VSTRING &param);
int cmd_del_bucket_tag(const VSTRING &param);
int cmd_list_mul_obj(const VSTRING &param);

//for test
int cmd_pwrite_object(const VSTRING& param);
int cmd_pread_object(const VSTRING& param);
//end for test


int cmd_put_object(const VSTRING& param);
int cmd_get_object(const VSTRING& param);
int cmd_del_object(const VSTRING& param);
int cmd_head_object(const VSTRING& param);

const char* rc_addr = NULL;
const char* nsip = NULL;
const char* krs_addr = NULL;
MetaType g_meta_type = META_RAW;

static tfs::common::BasePacketStreamer gstreamer;
static tfs::message::MessageFactory gfactory;



int main(int argc, char* argv[])
{
  int32_t i;
  int ret = TFS_SUCCESS;
  bool directly = false;
  bool set_log_level = false;

  // analyze arguments
  while ((i = getopt(argc, argv, "s:r:k:nih")) != EOF)
  {
    switch (i)
    {
      case 'n':
        set_log_level = true;
        break;
      case 's':
        nsip = optarg;
        break;
      case 'r':
        rc_addr = optarg;
        break;
      case 'k':
        krs_addr = optarg;
        break;
      case 'i':
        directly = true;
        break;
      case 'h':
      default:
        usage(argv[0]);
        return TFS_ERROR;
    }
  }

  if (set_log_level)
  {
    TBSYS_LOGGER.setLogLevel("ERROR");
  }

  if (NULL == nsip && NULL == rc_addr & NULL == krs_addr)
  {
    usage(argv[0]);
    return TFS_ERROR;
  }

  gstreamer.set_packet_factory(&gfactory);
  NewClientManager::get_instance().initialize(&gfactory, &gstreamer);

  if (krs_addr != NULL)
  {
    if (rc_addr == NULL)
    {
      usage(argv[0]);
      return TFS_ERROR;
    }
    strcpy(app_key, default_app_key);
    g_meta_type = META_KV;
  }
  else if (rc_addr != NULL)
  {
    strcpy(app_key, default_app_key);
    g_meta_type = META_NAME;
  }
  else if (nsip != NULL)
  {
    g_tfs_client = TfsClientImpl::Instance();
    ret = g_tfs_client->initialize(nsip, DEFAULT_BLOCK_CACHE_TIME, 1000, false);
    if (ret != TFS_SUCCESS)
    {
      fprintf(stderr, "init tfs client fail, ret: %d\n", ret);
      return ret;
    }
    g_meta_type = META_RAW;
  }

  init();

  if (optind >= argc)
  {
    signal(SIGINT, sign_handler);
    signal(SIGTERM, sign_handler);
    main_loop();
  }
  else // has other params
  {
    int32_t i = 0;
    if (directly)
    {
      for (i = optind; i < argc; i++)
      {
        do_cmd(argv[i]);
      }
    }
    else
    {
      VSTRING param;
      for (i = optind; i < argc; i++)
      {
        param.clear();
        param.push_back(argv[i]);
        cmd_batch_file(param);
      }
    }
  }
  if (g_tfs_client != NULL)
  {
    g_tfs_client->destroy();
  }
  return TFS_SUCCESS;
}

static void usage(const char* name)
{
  fprintf(stderr,
          "Usage: a) %s -s nsip [-n] [-i] [-h] raw tfs client interface(without rc). \n"
          "       b) %s -r rcip [-n] [-i] [-h] name meta client interface(with rc). \n"
          "       c) %s -k krsip -r rcip [-n] [-i] [-h] kv meta client interface. \n"
          "       -s nameserver ip port\n"
          "       -r rcserver ip port\n"
          "       -k kvrootserver ip port\n"
          "       -n set log level\n"
          "       -i directly execute the command\n"
          "       -h help\n",
          name, name, name);
}

static void sign_handler(const int32_t sig)
{
  switch (sig)
  {
  case SIGINT:
  case SIGTERM:
    fprintf(stderr, "\nTFS> ");
      break;
  }
}

void init()
{
  g_cmd_map["help"] = CmdNode("help", "show help info", 0, 0, cmd_show_help);
  g_cmd_map["quit"] = CmdNode("quit", "quit", 0, 0, cmd_quit);
  g_cmd_map["exit"] = CmdNode("exit", "exit", 0, 0, cmd_quit);
  g_cmd_map["@"] = CmdNode("@ file", "batch run command in file", 1, 1, cmd_batch_file);
  g_cmd_map["batch"] = CmdNode("batch file", "batch run command in file", 1, 1, cmd_batch_file);
  switch (g_meta_type)
  {
  case META_RAW:
    g_cmd_map["cd"] = CmdNode("cd [directory]", "change work directory", 0, 1, cmd_cd);
    g_cmd_map["ls"] = CmdNode("ls [directory]", "list directory content", 0, 1, cmd_ls);
    g_cmd_map["pwd"] = CmdNode("pwd", "print current directory", 0, 0, cmd_pwd);
    g_cmd_map["put"] = CmdNode("put localfile [tfsname [suffix] [force]]", "put file to tfs", 1, 4, cmd_put_file);
    g_cmd_map["uput"] = CmdNode("uput localfile [tfsname [suffix] [force]]", "unique put file to tfs", 1, 4, cmd_uput_file);
    // put large file not support update now
    g_cmd_map["putl"] = CmdNode("putl localfile [suffix]", "put file to tfs large file", 1, 2, cmd_put_large_file);
    g_cmd_map["get"] = CmdNode("get tfsname localfile", "get file from tfs", 2, 2, cmd_get_file);
    g_cmd_map["rm"] = CmdNode("rm tfsname", "remove tfs file", 1, 1, cmd_remove_file);
    g_cmd_map["urm"] = CmdNode("urm tfsname", "unique remove tfs file", 1, 1, cmd_uremove_file);
    g_cmd_map["undel"] = CmdNode("undel tfsname", "undelete tfs file", 1, 1, cmd_undel_file);
    g_cmd_map["hide"] = CmdNode("hide tfsname [action]", "hide tfs file", 1, 2, cmd_hide_file);
    g_cmd_map["rename"] = CmdNode("rename tfsname newsuffix", "rename tfs file to new suffix", 2, 2, cmd_rename_file);
    g_cmd_map["stat"] = CmdNode("stat tfsname", "stat tfs file", 1, 1, cmd_stat_file);
    g_cmd_map["statblk"] = CmdNode("statblk blockid [serverip:port]", "stat a block", 1, 2, cmd_stat_blk);
    g_cmd_map["vcblk"] = CmdNode("vcblk serverip:port count", "visit count block", 2, 2, cmd_visit_count_blk);
    g_cmd_map["lsf"] = CmdNode("lsf blockid [detail] [serverip:port]" , "list file list in block", 1, 3, cmd_list_file_info);
    g_cmd_map["listblock"] = CmdNode("listblock blockid", "list block server list", 1, 1, cmd_list_block);
    g_cmd_map["cfi"] = CmdNode("cfi tfsname", "check file info", 1, 1, cmd_check_file_info);
    break;
  case META_NAME:
    g_cmd_map["put"] = CmdNode("put localfile [[suffix] [app_key]]", "put raw file to tfs", 1, 3, cmd_put_file_raw);
    g_cmd_map["putl"] = CmdNode("putl localfile [suffix [app_key]]", "put raw file to tfs large file", 1, 3, cmd_put_large_file_raw);
    g_cmd_map["get"] = CmdNode("get tfsname localfile [app_key]", "get raw file from tfs", 2, 3, cmd_get_file_raw);
    g_cmd_map["stat"] = CmdNode("stat tfsname [app_key]", "stat raw tfs file", 1, 2, cmd_stat_file_raw);
    g_cmd_map["rm"] = CmdNode("rm tfsname [app_key]", "remove raw tfs file", 1, 2, cmd_remove_file_raw);
    g_cmd_map["undel"] = CmdNode("undel tfsname [app_key]", "undelete raw tfs file", 1, 2, cmd_unremove_file_raw);
    g_cmd_map["hide"] = CmdNode("hide tfsname [action [app_key]]", "hide raw tfs file, param 4 for hide and 6 for unhide", 1, 3, cmd_hide_file_raw);

    g_cmd_map["stat_file_meta"] = CmdNode("stat_file_meta full_path_file_name rootserver_addr [app_id uid], optional param should be in order",
        "get fragment info for meta file", 2, 4, cmd_stat_file_meta);
    g_cmd_map["ls_dir_meta"] = CmdNode("ls_dir_meta full_path_dir_name [ app_key app_id uid ], optional param should be in order",
        "ls files and dirs in full_path_dir_name", 1, 4, cmd_ls_dir_meta);
    g_cmd_map["ls_file_meta"] = CmdNode("ls_file_meta full_path_file_name [ app_key app_id uid ], optional param should be in order",
        "ls file info and frag infos of full_path_file_name", 1, 4, cmd_ls_file_meta);
    g_cmd_map["create_dir_meta"] = CmdNode("create_dir_meta full_path_dir_name [ app_key uid ], optional param should be in order",
        "create full_path_dir_name", 1, 3, cmd_create_dir_meta);
    g_cmd_map["create_file_meta"] = CmdNode("create_file_meta full_path_file_name [ app_key uid ], optional param should be in order",
        "create full_path_file_name", 1, 3, cmd_create_file_meta);
    g_cmd_map["rm_dir_meta"] = CmdNode("rm_dir_meta full_path_dir_name [ app_key uid ], optional param should be in order",
        "rm full_path_dir_name", 1, 3, cmd_rm_dir_meta);
    g_cmd_map["rm_file_meta"] = CmdNode("rm_file_meta full_path_file_name [ app_key uid ], optional param should be in order",
        "rm full_path_file_name", 1, 3, cmd_rm_file_meta);
    g_cmd_map["put_meta"] = CmdNode("put_meta localfile remotefile [ app_key app_id uid ], optional param should be in order",
        "put localfile to remotefile", 2, 5, cmd_put_file_meta);
    g_cmd_map["get_meta"] = CmdNode("get_meta remotefile localfile [ app_key app_id uid ], optional param should be in order",
        "get remotefile to localfile", 2, 5, cmd_get_file_meta);
    g_cmd_map["is_dir_exist_meta"] = CmdNode("is_dir_exist_meta full_path_dir_name [ app_key app_id uid ], optional param should be in order",
        "check if dir exist", 1, 4, cmd_is_dir_exist_meta);
    g_cmd_map["is_file_exist_meta"] = CmdNode("is_file_exist_meta full_path_file_name [ app_key app_id uid ], optional param should be in order",
        "check if file exist", 1, 4, cmd_is_file_exist_meta);
    break;
  case META_KV:
    g_cmd_map["put_bucket"] = CmdNode("put_bucket bucket_name owner_id", "create a bucket", 2, 2, cmd_put_bucket);
    g_cmd_map["get_bucket"] = CmdNode("get_bucket bucket_name [ prefix start_key delimiter limit ]", "get a bucket(list object)", 1, 5, cmd_get_bucket);
    g_cmd_map["del_bucket"] = CmdNode("del_bucket bucket_name", "delete a bucket", 1, 1, cmd_del_bucket);
    g_cmd_map["head_bucket"] = CmdNode("head_bucket bucket_name", "stat a bucket", 1, 1, cmd_head_bucket);

    g_cmd_map["list_mul_obj"] = CmdNode("list_mul_obj bucket_name [prefix start_key start_id delimiter limit]", "list multipart objects", 1, 6, cmd_list_mul_obj);

    g_cmd_map["put_bucket_tag"] = CmdNode("put_bucket_tag bucket_name map_size key value [key value]", "put bucket tag", 4, 22, cmd_put_bucket_tag);
    g_cmd_map["get_bucket_tag"] = CmdNode("get_bucket_tag bucket_name", "get bucket tag", 1, 1, cmd_get_bucket_tag);
    g_cmd_map["del_bucket_tag"] = CmdNode("del_bucket_tag bucket_name", "del bucket tag", 1, 1, cmd_del_bucket_tag);

    //for test
    g_cmd_map["pwrite_object"] =  CmdNode("pwrite_object bucket_name object_name local_file owner_id offset length", "put a object", 4, 6, cmd_pwrite_object);
    g_cmd_map["pread_object"] = CmdNode("pread_object bucket_name object_name local_file owner_id offset length", "get a object", 4, 6, cmd_pread_object);
    //end for test

    g_cmd_map["put_object"] = CmdNode("put_object bucket_name object_name local_file owner_id map_size [key value]", "put a object", 5, 25, cmd_put_object);
    g_cmd_map["get_object"] = CmdNode("get_object bucket_name object_name local_file", "get a object", 3, 3, cmd_get_object);
    g_cmd_map["del_object"] = CmdNode("del_object bucket_name object_name", "delete a object", 2, 2, cmd_del_object);
    g_cmd_map["head_object"] = CmdNode("head_object bucket_name object_name", "stat a object", 2, 2, cmd_head_object);
    break;
  }
}

int main_loop()
{
#ifdef _WITH_READ_LINE
  char* cmd_line = NULL;
  rl_attempted_completion_function = tfscmd_completion;
#else
  char cmd_line[MAX_CMD_SIZE];
#endif

  int ret = TFS_ERROR;
  while (1)
  {
#ifdef _WITH_READ_LINE
    cmd_line = readline("TFS> ");
    if (!cmd_line)
#else
      fprintf(stdout, "TFS> ");
    if (NULL == fgets(cmd_line, MAX_CMD_SIZE, stdin))
#endif
    {
      continue;
    }
    ret = do_cmd(cmd_line);
#ifdef _WITH_READ_LINE
    free(cmd_line);
#endif
    if (TFS_CLIENT_QUIT == ret)
    {
      break;
    }
  }
  return TFS_SUCCESS;
}

int32_t do_cmd(char* key)
{
  char* token;
  while (' ' == *key)
  {
    key++;
  }
  token = key + strlen(key);
  while (' ' == *(token - 1) || '\n' == *(token - 1) || '\r' == *(token - 1))
  {
    token--;
  }
  *token = '\0';
  if ('\0' == key[0])
  {
    return TFS_SUCCESS;
  }

#ifdef _WITH_READ_LINE
  // not blank line, add to history
  add_history(key);
#endif

  token = strchr(key, ' ');
  if (token != NULL)
  {
    *token = '\0';
  }

  string cur_cmd = Func::str_to_lower(key);
  STR_FUNC_MAP_ITER it = g_cmd_map.find(cur_cmd);

  if (it == g_cmd_map.end())
  {
    fprintf(stderr, "unknown command. \n");
    return TFS_ERROR;
  }

  if (token != NULL)
  {
    token++;
    key = token;
  }
  else
  {
    key = NULL;
  }

  VSTRING param;
  param.clear();
  while ((token = strsep(&key, " ")) != NULL)
  {
    if ('\0' == token[0])
    {
      continue;
    }
    param.push_back(token);
  }

  // check param count
  int32_t param_cnt = param.size();
  if (param_cnt < it->second.min_param_cnt_ || param_cnt > it->second.max_param_cnt_)
  {
    fprintf(stderr, "%s\t\t%s\n\n", it->second.syntax_, it->second.info_);
    return TFS_ERROR;
  }

  return it->second.func_(param);
}

const char* canonical_param(const string& param)
{
  const char* ret_param = param.c_str();
  if (NULL != ret_param &&
      (strlen(ret_param) == 0 ||
       strcasecmp(ret_param, "null") == 0))
  {
    ret_param = NULL;
  }
  return ret_param;
}

// expand ~ to HOME. modify argument
const char* expand_path(string& path)
{
  if (path.size() > 0 && '~' == path.at(0) &&
      (1 == path.size() ||                      // just one ~
       (path.size() > 1 && '/' == path.at(1)))) // like ~/xxx
  {
    char* home_path = getenv("HOME");
    if (NULL == home_path)
    {
      fprintf(stderr, "can't get HOME path: %s\n", strerror(errno));
    }
    else
    {
      path.replace(0, 1, home_path);
    }
  }
  return path.c_str();
}

int put_file_ex(const VSTRING& param, const bool unique, const bool is_large)
{
  int32_t size = param.size();
  const char* local_file = expand_path(const_cast<string&>(param[0]));
  const char* tfs_name = NULL;
  const char* suffix = NULL;
  int32_t flag = T_DEFAULT;
  int ret = TFS_SUCCESS;
  char ret_tfs_name[TFS_FILE_LEN];
  ret_tfs_name[0] = '\0';


  if (is_large)
  {
    if (size > 1)
    {
      suffix = canonical_param(param[1]);
    }
    flag |= T_LARGE;
  }
  else
  {
    if (size > 1)
    {
      tfs_name = canonical_param(param[1]);
    }

    if (size > 2)
    {
      suffix = canonical_param(param[2]);
    }

    if (size > 3 &&
        param[3] == "force")
    {
      flag |= T_NEWBLK;
    }
  }

  if (unique)
  {
    // TODO: save unique
    if (tfs_name != NULL)
    {
      ret = g_tfs_client->save_file_update(local_file, flag, tfs_name, suffix) < 0 ? TFS_ERROR : TFS_SUCCESS;
    }
    else
    {
      ret = g_tfs_client->save_file(ret_tfs_name, TFS_FILE_LEN, local_file, flag, suffix) < 0 ?
          TFS_ERROR : TFS_SUCCESS;
    }
  }
  else
  {
    if (tfs_name != NULL)
    {
      ret = g_tfs_client->save_file_update(local_file, flag, tfs_name, suffix) < 0 ? TFS_ERROR : TFS_SUCCESS;
    }
    else
    {
      ret = g_tfs_client->save_file(ret_tfs_name, TFS_FILE_LEN, local_file, flag, suffix) < 0 ?
          TFS_ERROR : TFS_SUCCESS;
    }
  }

  //printf("tfs_name: %s, ret_tfs_name: %s\n", tfs_name, ret_tfs_name);
  ToolUtil::print_info(ret, "put %s => %s", local_file, tfs_name != NULL ? FSName(tfs_name, suffix).get_name() : ret_tfs_name);
  return ret;
}

int put_file_raw_ex(const VSTRING& param, const bool is_large)
{
  int32_t size = param.size();
  const char* local_file = expand_path(const_cast<string&>(param[0]));
  char* tfs_name = NULL;
  const char* suffix = NULL;
  char appkey[257];
  //int32_t flag = T_DEFAULT;
  int ret = TFS_SUCCESS;
  char ret_tfs_name[TFS_FILE_LEN];
  ret_tfs_name[0] = '\0';

  if (size > 1)
  {
    suffix = canonical_param(param[1]);
  }

  if (size > 2)
  {
    //TBSYS_LOG(DEBUG, "app_key: %s", param[2].c_str());
    strncpy(appkey, canonical_param(param[2]), 256);
    appkey[256] = '\0';
  }
  else
  {
    // default app_key = "tfscom"
    strcpy(appkey, app_key);
  }

  //login with rc and app_key
  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.save_file(local_file, ret_tfs_name, TFS_FILE_LEN, suffix, is_large) < 0 ? TFS_ERROR : TFS_SUCCESS;
  }

  //printf("tfs_name: %s, ret_tfs_name: %s\n", tfs_name, ret_tfs_name);

  ToolUtil::print_info(ret, "put %s => %s", local_file, tfs_name != NULL ? FSName(tfs_name, suffix).get_name() : ret_tfs_name);
  return ret;
}



int remove_file_ex(const VSTRING& param, const int32_t unique)
{
  const char* tfs_name = canonical_param(param[0]);
  int ret = TFS_SUCCESS;
  int64_t file_size = 0;
  if (unique)
  {
    // TODO: unlink_unique
    ret = g_tfs_client->unlink(file_size, tfs_name, NULL);
  }
  else
  {
    ret = g_tfs_client->unlink(file_size, tfs_name, NULL);
  }

  ToolUtil::print_info(ret, "unlink %s", tfs_name);

  return ret;
}

int remove_file_raw_ex(const VSTRING& param, TfsUnlinkType type)
{
  const char* tfs_name = canonical_param(param[0]);
  char appkey[257];
  int ret = TFS_SUCCESS;


  int size = param.size();
  if (size > 1)
  {
    //TBSYS_LOG(DEBUG, "app_key: %s", param[1].c_str());
    strncpy(appkey, canonical_param(param[1]), 256);
    appkey[256] = '\0';
  }
  else
  {
    //default app_key = "tfscom"
    strcpy(appkey, app_key);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init fail, ret: %d", ret);
  }
  else
  {
    ret = impl.unlink(tfs_name, NULL, type);
  }

  if (type == DELETE)
  {
    ToolUtil::print_info(ret, "del %s", tfs_name);
  }
  else if (type == UNDELETE)
  {
    ToolUtil::print_info(ret, "undel %s", tfs_name);
  }

  return ret;
}



int cmd_ls(const VSTRING& param)
{
  int32_t size = param.size();
  const char* path = (1 == size) ? param.at(0).c_str() : ".";
  char sys_cmd[MAX_CMD_SIZE];
  // just use system tool ls, maybe DIY
  snprintf(sys_cmd, MAX_CMD_SIZE, "ls -FCl %s", path);
  return system(sys_cmd);
}

int cmd_pwd(const VSTRING&)
{
  char dir[MAX_PATH_LENGTH], *path = NULL;

  path = getcwd(dir, MAX_PATH_LENGTH - 1);
  if (NULL == path)
  {
    fprintf (stderr, "can't get current work directory: %s\n", strerror(errno));
    return TFS_ERROR;
  }
  fprintf(stdout, "%s\n", dir);
  return TFS_SUCCESS;
}

int cmd_cd(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  int32_t size = param.size();
  const char* dest_dir = (1 == size) ? expand_path(const_cast<string&>(param[0])) : getenv("HOME");

  if (NULL == dest_dir)
  {
    fprintf(stderr, "no directory argument and HOME not found\n\n");
    ret = TFS_ERROR;
  }
  else if (chdir(dest_dir) == -1)
  {
    fprintf(stderr, "can't change directory %s: %s\n", dest_dir, strerror(errno));
    ret = TFS_ERROR;
  }
  cmd_pwd(param);
  return ret;
}

int cmd_quit(const VSTRING&)
{
  return TFS_CLIENT_QUIT;
}

int cmd_show_help(const VSTRING&)
{
  return ToolUtil::show_help(g_cmd_map);
}

int cmd_put_file(const VSTRING& param)
{
  return put_file_ex(param, false);
}

int cmd_uput_file(const VSTRING& param)
{
  return put_file_ex(param, true);
}

int cmd_put_large_file(const VSTRING& param)
{
  return put_file_ex(param, false, true);
}

int cmd_get_file(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);
  const char* local_file = expand_path(const_cast<string&>(param[1]));

  int ret = g_tfs_client->fetch_file(local_file, tfs_name, NULL);

  ToolUtil::print_info(ret, "fetch %s => %s", tfs_name, local_file);

  return ret;
}

int cmd_remove_file(const VSTRING& param)
{
  return remove_file_ex(param, 0);
}

int cmd_uremove_file(const VSTRING& param)
{
  return remove_file_ex(param, 1);
}

int cmd_undel_file(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);
  int64_t file_size = 0;
  int ret = g_tfs_client->unlink(file_size, tfs_name, NULL, UNDELETE);

  ToolUtil::print_info(ret, "undel %s", tfs_name);

  return TFS_SUCCESS;
}

int cmd_hide_file(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);

  TfsUnlinkType unlink_type = CONCEAL;
  if (param.size() > 1)
  {
    unlink_type = static_cast<TfsUnlinkType>(atoi(param[1].c_str()));
  }

  int64_t file_size = 0;
  int ret = g_tfs_client->unlink(file_size, tfs_name, NULL, unlink_type);

  ToolUtil::print_info(ret, "hide %s %d", tfs_name, unlink_type);

  return ret;
}

int cmd_rename_file(const VSTRING&)
{
  // TODO: rename
  // const char* tfs_name = canonical_param(param[0]);
  // const char* new_suffix = canonical_param(param[1]);
  // int ret = tfs_client->rename(tfs_name, NULL, new_suffix);

  // print_info(ret, "rename %s to new suffix %s", tfs_name, new_suffix);

  // return ret;
  return TFS_SUCCESS;
}

int cmd_stat_file(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);

  TfsFileStat file_stat;
  int ret = g_tfs_client->stat_file(&file_stat, tfs_name, NULL, FORCE_STAT);

  ToolUtil::print_info(ret, "stat %s", tfs_name);

  if (TFS_SUCCESS == ret)
  {
    FSName fsname(tfs_name, NULL);
    fprintf(stdout,
            "  FILE_NAME:     %s\n"
            "  BLOCK_ID:      %u\n"
            "  FILE_ID:       %" PRI64_PREFIX "u\n"
            "  OFFSET:        %d\n"
            "  SIZE:          %"PRI64_PREFIX"d\n"
            "  OCCUPY SIZE:   %"PRI64_PREFIX"d\n"
            "  MODIFIED_TIME: %s\n"
            "  CREATE_TIME:   %s\n"
            "  STATUS:        %d\n"
            "  CRC:           %u\n",
            tfs_name, fsname.get_block_id(), file_stat.file_id_,
            file_stat.offset_, file_stat.size_, file_stat.usize_,
            Func::time_to_str(file_stat.modify_time_).c_str(),
            Func::time_to_str(file_stat.create_time_).c_str(),
            file_stat.flag_, file_stat.crc_);
  }

  // uint64_t id = file_stat.size_;
  // id <<= 32;
  // id |= file_info.crc_;
  //  uint32_t tindex = static_cast<uint32_t>(id & 0xFFFFFFFF);
  //  printf("  TABLE:         select * from t%d where id='%" PRI64_PREFIX "u'\n", tindex % TABLE_COUNT, id);

  return ret;
}

int cmd_batch_file(const VSTRING& param)
{
  const char* batch_file = expand_path(const_cast<string&>(param[0]));
  FILE* fp = fopen(batch_file, "rb");
  int ret = TFS_SUCCESS;
  if (fp == NULL)
  {
    fprintf(stderr, "open file error: %s\n\n", batch_file);
    ret = TFS_ERROR;
  }
  else
  {
    int32_t error_count = 0;
    int32_t count = 0;
    VSTRING params;
    char buffer[MAX_CMD_SIZE];
    while (fgets(buffer, MAX_CMD_SIZE, fp))
    {
      if ((ret = do_cmd(buffer)) == TFS_ERROR)
      {
        error_count++;
      }
      if (++count % 100 == 0)
      {
        fprintf(stdout, "tatol: %d, %d errors.\r", count, error_count);
        fflush(stdout);
      }
      if (TFS_CLIENT_QUIT == ret)
      {
        break;
      }
    }
    fprintf(stdout, "tatol: %d, %d errors.\n\n", count, error_count);
    fclose(fp);
  }
  return TFS_SUCCESS;
}

int cmd_stat_blk(const VSTRING& param)
{
  int ret = TFS_ERROR;

  uint64_t server_id = 0;
  uint32_t block_id = 0;

  if ((block_id = atoi(param[0].c_str())) <= 0)
  {
    fprintf(stderr, "invalid blockid: %u\n", block_id);
  }

  if (param.size() > 2)
  {
    server_id = Func::get_host_ip(param[1].c_str());
  }
  else
  {
    VUINT64 ds_list;
    ret = ToolUtil::get_block_ds_list(g_tfs_client->get_server_id(), block_id, ds_list);
    if (ret != TFS_SUCCESS)
    {
      fprintf(stderr, "get ds list failed. block_id: %u, ret: %d\n", block_id, ret);
      return ret;
    }
    server_id = ds_list[0];
  }

  DsTask ds_task(server_id, g_tfs_client->get_cluster_id());
  ds_task.block_id_ = block_id;
  ret = DsLib::get_block_info(ds_task);

  return ret;
}

int cmd_visit_count_blk(const VSTRING& param)
{
  uint64_t server_id = 0;
  int32_t count = 0;
  int ret = TFS_ERROR;

  if ((server_id  = Func::get_host_ip(param[0].c_str())) <= 0)
  {
    fprintf(stderr, "invalid ds address: %s\n", param[0].c_str());
  }
  else if ((count = atoi(param[1].c_str())) <= 0)
  {
    fprintf(stderr, "invalid count: %d\n", count);
  }
  else
  {
    DsTask ds_task(server_id, g_tfs_client->get_cluster_id());
    ds_task.num_row_ = count;
    ret = DsLib::get_server_status(ds_task);
  }
  return ret;
}

int cmd_list_file_info(const VSTRING& param)
{
  uint32_t block_id = 0;
  uint64_t server_id = 0;

  int32_t show_detail = 0;

  int ret = TFS_ERROR;
  if ((block_id = atoi(param[0].c_str())) <= 0)
  {
    fprintf(stderr, "invalid blockid: %u\n", block_id);
    return ret;
  }

  if (param.size() > 2)
  {
    server_id = Func::get_host_ip(param[2].c_str());
  }
  else
  {
    VUINT64 ds_list;
    ret = ToolUtil::get_block_ds_list(g_tfs_client->get_server_id(), block_id, ds_list);
    if (ret != TFS_SUCCESS)
    {
      fprintf(stderr, "get ds list failed. block_id: %u, ret: %d\n", block_id, ret);
      return ret;
    }
    server_id = ds_list[0];
  }

  if (0 != server_id)
  {
    if (param.size() > 1 && 0 == strcmp(param[1].c_str(), "detail"))
    {
      show_detail = 1;
    }
    DsTask ds_task(server_id, g_tfs_client->get_cluster_id());
    ds_task.block_id_ = block_id;
    ds_task.mode_ = show_detail;
    ret = DsLib::list_file(ds_task);
  }

  return ret;
}

int cmd_list_block(const VSTRING& param)
{
  uint32_t block_id = atoi(param[0].c_str());
  int ret = TFS_ERROR;

  if (block_id <= 0)
  {
    fprintf(stderr, "invalid block id: %u\n", block_id);
  }
  else
  {
    VUINT64 ds_list;
    ret = ToolUtil::get_block_ds_list(g_tfs_client->get_server_id(), block_id, ds_list);
    ToolUtil::print_info(ret, "list block %u", block_id);

    if (TFS_SUCCESS == ret)
    {
      int32_t ds_size = ds_list.size();
      fprintf(stdout, "------block: %u, has %d replicas------\n", block_id, ds_size);
      for (int32_t i = 0; i < ds_size; ++i)
      {
        fprintf(stdout, "block: %u, (%d)th server: %s \n", block_id, i, tbsys::CNetUtil::addrToString(ds_list[i]).c_str());
      }
    }
  }
  return ret;
}

int cmd_check_file_info(const VSTRING& param)
{
  int ret = TFS_ERROR;
  FSName fsname(canonical_param(param[0]));

  if (!fsname.is_valid())
  {
    fprintf(stderr, "file name is invalid: %s\n", param[0].c_str());
  }
  else
  {
    VUINT64 ds_list;
    ret = ToolUtil::get_block_ds_list(g_tfs_client->get_server_id(), fsname.get_block_id(), ds_list);
    if (ret != TFS_SUCCESS)
    {
      fprintf(stderr, "get block info fail, ret: %d\n", ret);
    }
    else
    {
      DsTask ds_task(0, g_tfs_client->get_cluster_id());
      int32_t ds_size = ds_list.size();
      for (int32_t i = 0; i < ds_size; ++i)
      {
        ds_task.server_id_ = ds_list[i];
        ds_task.block_id_ = fsname.get_block_id();
        ds_task.new_file_id_ = fsname.get_file_id();
        ret = DsLib::check_file_info(ds_task);
      }
    }
  }
  return ret;
}

int cmd_stat_file_raw(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);
  char appkey[257];
  int size = param.size();

  if (size > 1)
  {
    //TBSYS_LOG(DEBUG, "app_key: %s", param[1].c_str());
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  TfsFileStat file_stat;

  RcClientImpl impl;
  int ret = impl.initialize(rc_addr, appkey, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc init fail, ret: %d", ret);
  }
  else
  {
    int fd = -1;
    if (tfs_name[0] == 'L')
    {
      fd = impl.open(tfs_name, NULL, RcClient::READ, true);
    }
    else
    {
      fd = impl.open(tfs_name, NULL, RcClient::READ);
    }
    if (fd < 0)
    {
      ret = TFS_ERROR;
      TBSYS_LOG(DEBUG, "%s open fail, return fd: %d", tfs_name, fd);
    }
    else
    {
      if ((ret = impl.fstat(fd, &file_stat, FORCE_STAT)) != TFS_SUCCESS)
      {
        TBSYS_LOG(DEBUG, "stat %s fail, return %d", tfs_name, ret);
      }
      int re = impl.close(fd, const_cast<char*>(tfs_name), TFS_FILE_LEN);
      if (TFS_SUCCESS != re)
      {
        TBSYS_LOG(DEBUG, "close %s fail, return %d", tfs_name, re);
      }
    }
  }

  ToolUtil::print_info(ret, "stat %s", tfs_name);

  if (TFS_SUCCESS == ret)
  {
    FSName fsname(tfs_name, NULL);
    fprintf(stdout,
            "  FILE_NAME:     %s\n"
            "  BLOCK_ID:      %u\n"
            "  FILE_ID:       %" PRI64_PREFIX "u\n"
            "  OFFSET:        %d\n"
            "  SIZE:          %"PRI64_PREFIX"d\n"
            "  OCCUPY SIZE:   %"PRI64_PREFIX"d\n"
            "  MODIFIED_TIME: %s\n"
            "  CREATE_TIME:   %s\n"
            "  STATUS:        %d\n"
            "  CRC:           %u\n",
            tfs_name, fsname.get_block_id(), file_stat.file_id_,
            file_stat.offset_, file_stat.size_, file_stat.usize_,
            Func::time_to_str(file_stat.modify_time_).c_str(),
            Func::time_to_str(file_stat.create_time_).c_str(),
            file_stat.flag_, file_stat.crc_);
  }

  // uint64_t id = file_stat.size_;
  // id <<= 32;
  // id |= file_info.crc_;
  //  uint32_t tindex = static_cast<uint32_t>(id & 0xFFFFFFFF);
  //  printf("  TABLE:         select * from t%d where id='%" PRI64_PREFIX "u'\n", tindex % TABLE_COUNT, id);

  return ret;
}

int cmd_stat_file_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* file_path = expand_path(const_cast<string&>(param[0]));
  char rs_addr[257];
  FragInfo frag_info_;

  int size = param.size();
  if (size > 1)
  {
    strncpy(rs_addr, param[1].c_str(), 256);
    rs_addr[256] = '\0';
    printf("rs_addr: %s\n", rs_addr);
  }

  if (size > 2)
  {
    app_id = strtoll(param[2].c_str(), NULL, 10);
  }
  if (size > 3)
  {
    uid = strtoll(param[3].c_str(), NULL, 10);
  }

  NameMetaClientImpl impl;
  ret = impl.initialize(rs_addr);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client login rs_addr: %s fail, ret: %d", rs_addr, ret);
  }
  else
  {
    ret = impl.read_frag_info(app_id, uid, file_path, frag_info_);
  }

  //stat the detail
  ToolUtil::print_info(ret, "stat %s", file_path);
  if (TFS_SUCCESS == ret)
  {
    int32_t cluster_id = frag_info_.cluster_id_;
    std::vector<FragMeta> vFragMeta = frag_info_.v_frag_meta_;
    std::vector<FragMeta>::iterator iter;
    fprintf(stdout, "  FILE_NAME\t  BLOCK_ID\t  FILE_ID\t  OFFSET\t  SIZE\n");
    for (iter = vFragMeta.begin(); iter < vFragMeta.end(); iter++)
    {
      FSName fsname(iter->block_id_, iter->file_id_, cluster_id);
      fprintf(stdout,
            "  %s\t"
            "  %u\t"
            "  %" PRI64_PREFIX "u\t"
            "  %"PRI64_PREFIX"d\t"
            "  %d\n",
            fsname.get_name(), iter->block_id_, iter->file_id_, iter->offset_, iter->size_);
    }
  }

  return ret;
}

int cmd_ls_dir_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* dir_path = expand_path(const_cast<string&>(param[0]));
  int size = param.size();
  char appkey[257];
  TBSYS_LOG(DEBUG, "size: %d", size);
  if (size > 1)
  {
    TBSYS_LOG(DEBUG, "appkey: %s", param[1].c_str());
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    app_id = strtoll(param[2].c_str(), NULL, 10);
  }
  if (size > 3)
  {
    uid = strtoll(param[3].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    std::vector<FileMetaInfo> meta_info;
    std::vector<FileMetaInfo>::const_iterator it;
    ret = impl.ls_dir(app_id, uid, dir_path, meta_info);
    if (TFS_SUCCESS == ret)
    {
      for (it = meta_info.begin(); it != meta_info.end(); it++)
      {
        if (it->name_.size() > 0)
          fprintf(stdout, "name:%s\n", it->name_.data());
        fprintf(stdout, "pid %"PRI64_PREFIX"d id %"PRI64_PREFIX
            "d create_time %d modify_time %d size %"PRI64_PREFIX"d ver_no %d\n",
            it->pid_, it->id_, it->create_time_, it->modify_time_, it->size_, it->ver_no_);
      }
    }
  }
  return ret;
}


int cmd_ls_file_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  char appkey[257];
  const char* file_path = expand_path(const_cast<string&>(param[0]));
  int size = param.size();
  if (size > 1)
  {
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    app_id = strtoll(param[2].c_str(), NULL, 10);
  }
  if (size > 3)
  {
    uid = strtoll(param[3].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    FileMetaInfo file_info;
    ret = impl.ls_file(app_id, uid, file_path, file_info);
    if (TFS_SUCCESS == ret)
    {
      if (file_info.name_.size() > 0)
        fprintf(stdout, "name:%s\n", file_info.name_.data());
      fprintf(stdout, "pid %"PRI64_PREFIX"d id %"PRI64_PREFIX
          "d create_time %s modify_time %s size %"PRI64_PREFIX"d ver_no %d\n",
          file_info.pid_, file_info.id_, Func::time_to_str(file_info.create_time_).c_str(),
          Func::time_to_str(file_info.modify_time_).c_str(), file_info.size_, file_info.ver_no_);
    }
  }
  return ret;
}

int cmd_create_dir_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  char appkey[257];
  const char* dir_path = expand_path(const_cast<string&>(param[0]));
  int size = param.size();
  if (size > 1)
  {
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    uid = strtoll(param[2].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.create_dir_with_parents(uid, dir_path);
  }
  return ret;
}

int cmd_create_file_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* file_path = expand_path(const_cast<string&>(param[0]));
  char appkey[257];
  int size = param.size();
  if (size > 1)
  {
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    uid = strtoll(param[2].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.create_file(uid, file_path);
  }
  return ret;
}

int cmd_rm_dir_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* dir_path = expand_path(const_cast<string&>(param[0]));
  char appkey[257];
  int size = param.size();
  if (size > 1)
  {
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    uid = strtoll(param[2].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.rm_dir(uid, dir_path);
  }

  return ret;
}

int cmd_remove_file_raw(const VSTRING& param)
{
  return remove_file_raw_ex(param, static_cast<TfsUnlinkType>(0));
}

int cmd_unremove_file_raw(const VSTRING& param)
{
  return remove_file_raw_ex(param, static_cast<TfsUnlinkType>(2));
}

int cmd_hide_file_raw(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);
  char appkey[257];
  int size = param.size();

  TfsUnlinkType unlink_type = CONCEAL;
  if (size > 1)
  {
    unlink_type = static_cast<TfsUnlinkType>(atoi(param[1].c_str()));
  }

  if (size > 2)
  {
    strncpy(appkey, param[2].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  RcClientImpl impl;
  int ret = impl.initialize(rc_addr, appkey, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client login fail, ret: %d", ret);
  }
  else
  {
    ret = impl.unlink(tfs_name, NULL, unlink_type);
  }

  ToolUtil::print_info(ret, "hide %s %d", tfs_name, unlink_type);

  return ret;
}

int cmd_rm_file_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* file_path = expand_path(const_cast<string&>(param[0]));
  char appkey[257];
  int size = param.size();
  if (size > 1)
  {
    //TBSYS_LOG(DEBUG, "app_key: %s", param[1].c_str());
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    uid = strtoll(param[2].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.rm_file(uid, file_path);
  }

  return ret;
}

int cmd_put_file_raw(const VSTRING& param)
{
   return put_file_raw_ex(param);
}

int cmd_put_large_file_raw(const VSTRING& param)
{
  return put_file_raw_ex(param, true);
}


int cmd_put_file_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* local_file = expand_path(const_cast<string&>(param[0]));
  const char* file_path = expand_path(const_cast<string&>(param[1]));
  char appkey[257];
  int size = param.size();
  if (size > 2)
  {
    strncpy(appkey, param[2].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 3)
  {
    app_id = strtoll(param[3].c_str(), NULL, 10);
  }
  if (size > 4)
  {
    uid = strtoll(param[4].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    FILE* fp=fopen(local_file,"r");
    if(fp == NULL)
    {
      TBSYS_LOG(WARN, "open local file failed. local_file: %s, ret: %p", local_file, fp);
      ret = TFS_ERROR;
    }
    else
    {
      // create file if not exist
      FileMetaInfo file_meta_info;
      ret = impl.ls_file(app_id, uid, file_path, file_meta_info);
      if (ret != TFS_SUCCESS)
      {
        ret = impl.create_file(uid, file_path);
      }

      if (ret != TFS_SUCCESS)
      {
        TBSYS_LOG(DEBUG, "create file failed. file_path: %s, ret: %d", file_path, ret);
      }
      else
      {
        int fd = impl.open(app_id, uid, file_path, RcClient::WRITE);
        if (fd < 0)
        {
          TBSYS_LOG(WARN, "open file path failed. file_path: %s, ret: %d", file_path, fd);
          ret = TFS_ERROR;
        }
        else
        {
          int64_t offset = 0;
          char buffer[MAX_READ_SIZE];
          while (true)
          {
            int64_t rlen = fread(buffer,sizeof(char),MAX_READ_SIZE,fp);
            if (rlen <= 0)
            {
              break;
            }

            int64_t wlen = impl.pwrite(fd, buffer, rlen, offset);
            if (wlen != rlen)
            {
              TBSYS_LOG(ERROR, "write meta data failed. expect len: %"PRI64_PREFIX"d, return len: %"PRI64_PREFIX"d", rlen, wlen);
              ret = TFS_ERROR;
              break;
            }
            offset = -1;
            if (rlen < MAX_READ_SIZE)
            {
              break;
            }
          }
          impl.close(fd);
        }
      }
      fclose(fp);
    }
  }

  ToolUtil::print_info(ret, "put %s => %s", local_file, file_path);
  return ret;
}

int cmd_get_file_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* local_file = expand_path(const_cast<string&>(param[1]));
  const char* file_path = expand_path(const_cast<string&>(param[0]));
  char appkey[257];
  int size = param.size();
  if (size > 2)
  {
    strncpy(appkey, param[2].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 3)
  {
    app_id = strtoll(param[3].c_str(), NULL, 10);
  }
  if (size > 4)
  {
    uid = strtoll(param[4].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    int fd = impl.open(app_id, uid, file_path, RcClient::READ);
    if (fd < 0)
    {
      TBSYS_LOG(WARN, "open file path failed. file_path: %s, ret: %d", file_path, fd);
      ret = TFS_ERROR;
    }
    else
    {
      FILE* fp=fopen(local_file,"w+b");
      if(fp == NULL)
      {
        TBSYS_LOG(WARN, "open local file failed. local_file: %s, ret: %p", local_file, fp);
        ret = TFS_ERROR;
      }
      else
      {
        int64_t offset = 0;
        char buffer[MAX_READ_SIZE];
        while (true)
        {
          int rlen = impl.pread(fd, buffer, MAX_READ_SIZE, offset);
          if (rlen <= 0)
          {
            break;
          }

          offset += rlen;
          int wlen = fwrite(buffer,sizeof(char),rlen,fp);
          if (wlen != rlen)
          {
            TBSYS_LOG(ERROR, "write to local file error, expect len: %d, return len: %d", rlen, wlen);
            ret= TFS_ERROR;
            break;
          }
          if (rlen < MAX_READ_SIZE)
          {
            break;
          }
        }
        fclose(fp);
      }
      impl.close(fd);
    }
  }
  ToolUtil::print_info(ret, "get %s => %s", file_path, local_file);
  return ret;
}

int cmd_get_file_raw(const VSTRING& param)
{
  const char* tfs_name = canonical_param(param[0]);
  const char* local_file = expand_path(const_cast<string&>(param[1]));
  char appkey[257];
  int size = param.size();
  if (size > 2)
  {
    TBSYS_LOG(DEBUG, "appkey: %s", param[2].c_str());
    strncpy(appkey, param[2].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }
  int ret = TFS_SUCCESS;
  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.fetch_file(local_file, tfs_name, NULL);
  }

  ToolUtil::print_info(ret, "fetch %s => %s", tfs_name, local_file);

  return ret;
}

int cmd_is_dir_exist_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* dir_path = expand_path(const_cast<string&>(param[0]));
  char appkey[257];
  int size = param.size();
  TBSYS_LOG(DEBUG, "size: %d", size);
  if (size > 1)
  {
    //TBSYS_LOG(DEBUG, "appkey: %s", param[1].c_str());
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    app_id = strtoll(param[2].c_str(), NULL, 10);
  }
  if (size > 3)
  {
    uid = strtoll(param[3].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    std::vector<FileMetaInfo> meta_info;
    std::vector<FileMetaInfo>::const_iterator it;
    bool bRet = impl.is_dir_exist(app_id, uid, dir_path);
    fprintf(stdout, "dir: %s %s exist \n", dir_path, bRet ? "" : "not ");
  }
  return ret;
}

int cmd_is_file_exist_meta(const VSTRING& param)
{
  int ret = TFS_SUCCESS;
  const char* file_path = expand_path(const_cast<string&>(param[0]));
  char appkey[257];
  int size = param.size();
  TBSYS_LOG(DEBUG, "size: %d", size);
  if (size > 1)
  {
    //TBSYS_LOG(DEBUG, "appkey: %s", param[1].c_str());
    strncpy(appkey, param[1].c_str(), 256);
    appkey[256] = '\0';
  }
  else
  {
    strcpy(appkey, app_key);
  }

  if (size > 2)
  {
    app_id = strtoll(param[2].c_str(), NULL, 10);
  }
  if (size > 3)
  {
    uid = strtoll(param[3].c_str(), NULL, 10);
  }

  RcClientImpl impl;
  ret = impl.initialize(rc_addr, appkey, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "meta client init failed, ret: %d", ret);
  }
  else
  {
    bool bRet = impl.is_file_exist(app_id, uid, file_path);
    fprintf(stdout, "file: %s %s exist \n", file_path, bRet ? "" : "not ");
  }
  return ret;
}

int cmd_put_bucket(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  int64_t owner_id = strtoll(param[1].c_str(), NULL, 10);
  UserInfo user_info;
  user_info.owner_id_ = owner_id;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.put_bucket(bucket_name, user_info);
  }
  if (TFS_SUCCESS == ret)
  {
    ToolUtil::print_info(ret, "put bucket %s owner_id : %ld", bucket_name, owner_id);
  }
  return ret;
}

int cmd_get_bucket(const VSTRING& param)
{
  int size = param.size();

  int ret = TFS_SUCCESS;
  const char *bucket_name = NULL;
  const char *prefix = NULL;
  const char *start_key = NULL;
  char delimiter = DEFAULT_CHAR;
  int32_t limit = MAX_LIMIT;

  bucket_name = param[0].c_str();

  if (size > 1)
  {
    prefix = canonical_param(param[1]);
  }

  if (size > 2)
  {
    start_key = canonical_param(param[2]);
  }

  if (size > 3)
  {
    delimiter = canonical_param(param[3]) == NULL ? DEFAULT_CHAR : (param[3].size() == 1 ? param[3][0] : DEFAULT_CHAR);
  }

  if (size > 4)
  {
    limit = atoi(param[4].c_str());
  }

  vector<ObjectMetaInfo> v_object_meta_info;
  VSTRING v_object_name;
  set<string> s_common_prefix;
  int8_t is_truncated = 0;
  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  ret = impl.initialize(rc_addr, app_key, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.get_bucket(bucket_name, prefix, start_key, delimiter, limit,
        &v_object_meta_info, &v_object_name, &s_common_prefix, &is_truncated, user_info);
  }

  if (TFS_SUCCESS == ret)
  {
    printf("bucket: %s has %d common_prefix\n", bucket_name, static_cast<int>(s_common_prefix.size()));
    set<string>::iterator iter = s_common_prefix.begin();
    for (int i = 0; iter != s_common_prefix.end(); iter++, i++)
    {
      cout << i << ": " << *iter << endl;
    }
  }

  if (TFS_SUCCESS == ret)
  {
    printf("bucket: %s has %d objects\n", bucket_name, static_cast<int>(v_object_name.size()));
    for (int i = 0; i < static_cast<int>(v_object_name.size()); i++)
    {
      cout << i << ": " << v_object_name[i] << endl;
    }
  }

  printf("is_truncated: %d\n", is_truncated);
  //todo show info of objects
  ToolUtil::print_info(ret, "get bucket %s", bucket_name);
  return ret;
}

int cmd_list_mul_obj(const VSTRING& param)
{
  int size = param.size();

  int ret = TFS_SUCCESS;
  const char *bucket_name = NULL;
  const char *prefix = NULL;
  const char *start_key = NULL;
  const char *start_id = NULL;
  char delimiter = DEFAULT_CHAR;
  int32_t limit = MAX_LIMIT;

  bucket_name = param[0].c_str();

  if (size > 1)
  {
    prefix = canonical_param(param[1]);
  }

  if (size > 2)
  {
    start_key = canonical_param(param[2]);
  }

  if (size > 3)
  {
    start_id = canonical_param(param[3]);
  }

  if (size > 4)
  {
    delimiter = canonical_param(param[4]) == NULL ? DEFAULT_CHAR : (param[4].size() == 1 ? param[4][0] : DEFAULT_CHAR);
  }

  if (size > 5)
  {
    limit = atoi(param[5].c_str());
  }

  ListMultipartObjectResult list_multipart_object_result;
  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  ret = impl.initialize(rc_addr, app_key, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.list_multipart_object(bucket_name, prefix, start_key, start_id, delimiter, limit, &list_multipart_object_result, user_info);
  }

  if (TFS_SUCCESS == ret)
  {
    printf("bucket: %s has %d multipart common_prefix\n", bucket_name, static_cast<int>(list_multipart_object_result.s_common_prefix_.size()));
    set<string>::iterator iter = list_multipart_object_result.s_common_prefix_.begin();
    for (int i = 0; iter != list_multipart_object_result.s_common_prefix_.end(); iter++, i++)
    {
      cout << i << ": " << *iter << endl;
    }
  }

  if (TFS_SUCCESS == ret)
  {
    printf("bucket: %s has %d objects\n", bucket_name, static_cast<int>(list_multipart_object_result.v_object_upload_info_.size()));
    for (int i = 0; i < static_cast<int>(list_multipart_object_result.v_object_upload_info_.size()); i++)
    {
      cout << i << ": " << list_multipart_object_result.v_object_upload_info_[i].object_name_ << endl;
    }
  }

  printf("is_truncated: %d\n", list_multipart_object_result.is_truncated_);
  //todo show info of objects
  ToolUtil::print_info(ret, "get bucket %s", bucket_name);
  return ret;
}


int cmd_del_bucket(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.del_bucket(bucket_name, user_info);
  }

  ToolUtil::print_info(ret, "del bucket %s", bucket_name);

  return ret;
}

int cmd_head_bucket(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();

  BucketMetaInfo bucket_meta_info;
  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.head_bucket(bucket_name, &bucket_meta_info, user_info);
  }

  ToolUtil::print_info(ret, "head bucket %s", bucket_name);

  if (TFS_SUCCESS == ret)
  {
    printf("bucket: %s, create_time: %"PRI64_PREFIX"d, owner_id: %"PRI64_PREFIX"d\n",
        bucket_name, bucket_meta_info.create_time_, bucket_meta_info.owner_id_);
  }

  return ret;
}

int cmd_put_bucket_tag(const VSTRING& param)
{
  const char *bucket_name = param[0].c_str();
  int32_t map_size = atoi(param[1].c_str());

  int32_t size = param.size();

  int ret = TFS_SUCCESS;
  if (map_size * 2 != size - 2)
  {
    printf("param count error\n");
    return TFS_ERROR;
  }

  MAP_STRING bucket_tag_map;
  for (int32_t i = 0; i < map_size; i++)
  {
    const char *key = canonical_param(param[i*2+2]);
    const char *value = canonical_param(param[i*2+3]);
    if (NULL != key && NULL != value)
    {
      string tmp_key(key);
      string tmp_value(value);
      if (!(bucket_tag_map.insert(std::make_pair(tmp_key, tmp_value))).second)
      {
        printf("maybe insert duplicate key, fail\n");
        ret = TFS_ERROR;
        break;
      }
    }
  }

  if (TFS_SUCCESS == ret)
  {
    RcClientImpl impl;
    impl.set_kv_rs_addr(krs_addr);
    int ret = impl.initialize(rc_addr, app_key, app_ip);
    if (TFS_SUCCESS != ret)
    {
      TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
    }
    else
    {
      ret = impl.put_bucket_tag(bucket_name, bucket_tag_map);
    }
    if (TFS_SUCCESS == ret)
    {
      ToolUtil::print_info(ret, "put bucket: %s tag", bucket_name);
    }
  }
  return ret;
}

int cmd_get_bucket_tag(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);
  MAP_STRING bucket_tag_map;
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.get_bucket_tag(bucket_name, &bucket_tag_map);
  }
  if (TFS_SUCCESS == ret)
  {
    if (static_cast<int32_t>(bucket_tag_map.size()) > 0)
    {
      MAP_STRING_ITER iter = bucket_tag_map.begin();
      for (; iter != bucket_tag_map.end(); iter++)
      {
        printf("key: %s, value,: %s\n", (iter->first).c_str(), (iter->second).c_str());
      }
    }
    ToolUtil::print_info(ret, "get bucket: %s tag", bucket_name);
  }
  return ret;
}

int cmd_del_bucket_tag(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);
  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.del_bucket_tag(bucket_name);
  }
  if (TFS_SUCCESS == ret)
  {
    ToolUtil::print_info(ret, "del bucket: %s tag", bucket_name);
  }
  return ret;
}


int cmd_put_object(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  const char* object_name = param[1].c_str();
  const char* local_file = expand_path(const_cast<string&>(param[2]));
  int64_t owner_id = strtoll(param[3].c_str(), NULL, 10);
  int32_t map_size = atoi(param[4].c_str());

  int32_t size = param.size();

  int ret = TFS_SUCCESS;
  if (map_size * 2 != size - 5)
  {
    printf("param count error\n");
    return TFS_ERROR;
  }

  MAP_STRING meta_data;
  for (int32_t i = 0; i < map_size; i++)
  {
    const char *key = canonical_param(param[i*2+5]);
    const char *value = canonical_param(param[i*2+6]);
    if (NULL != key && NULL != value)
    {
      string tmp_key(key);
      string tmp_value(value);
      if (!(meta_data.insert(std::make_pair(tmp_key, tmp_value))).second)
      {
        printf("maybe insert duplicate key, fail\n");
        ret = TFS_ERROR;
        break;
      }
    }
  }

  UserInfo user_info;
  CustomizeInfo customize_info;
  if (meta_data.size() > 0)
  {
    customize_info.meta_data_ = meta_data;
  }
  user_info.owner_id_ = owner_id;
  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);

  if (TFS_SUCCESS == ret)
  {
    ret = impl.initialize(rc_addr, app_key, app_ip);
  }


  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.put_object(bucket_name, object_name, local_file, user_info, customize_info);
    ToolUtil::print_info(ret, "put object: %s, object: %s => %s owner_id: %"PRI64_PREFIX"d",
        bucket_name, object_name, local_file, owner_id);
  }
  return ret;
}

//for test
int cmd_pwrite_object(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  const char* object_name = param[1].c_str();
  const char* local_file = expand_path(const_cast<string&>(param[2]));
  int64_t owner_id = strtoll(param[3].c_str(), NULL, 10);
  int64_t offset = strtoll(param[4].c_str(), NULL, 10);
  int64_t length = strtoll(param[5].c_str(), NULL, 10);

  RcClientImpl impl;
  int ret = 0;
  UserInfo user_info;
  user_info.owner_id_ = owner_id;

  size_t size = 0;
  char buf[8*1<<20];
  FILE *fp = fopen(local_file, "r");
  if (NULL != fp)
  {
    while((size = fread(buf, sizeof(char), 256, fp)) <= 0)
      ;
    fclose(fp);
  }
  else
  {
    printf("open read from local file: %s fail\n", local_file);
    ret = TFS_ERROR;
  }

  //printf("owner_id: %"PRI64_PREFIX"d, offset: "PRI64_PREFIX"d, length: "PRI64_PREFIX"d, buf: %s\n", owner_id, offset, length, buf);

  impl.set_kv_rs_addr(krs_addr);

  if (TFS_SUCCESS == ret)
  {
    ret = impl.initialize(rc_addr, app_key, app_ip);
  }

  if (TFS_SUCCESS == ret)
  {
    ret = impl.pwrite_object(bucket_name, object_name, buf, offset, length, user_info);
  }

  printf("pwrite bucket: %s, object: %s, offset: %"PRI64_PREFIX"d, length: %"PRI64_PREFIX"d => %s, owner_id: %"PRI64_PREFIX"d\n", bucket_name, object_name, offset, length, local_file, owner_id);
  return ret;
}

int cmd_pread_object(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  const char* object_name = param[1].c_str();
  const char* local_file = expand_path(const_cast<string&>(param[2]));
  int64_t owner_id = strtoll(param[3].c_str(), NULL, 10);
  int64_t offset = strtoll(param[4].c_str(), NULL, 10);
  int64_t length = strtoll(param[5].c_str(), NULL, 10);

  RcClientImpl impl;
  ObjectMetaInfo object_meta_info;
  CustomizeInfo customize_info;
  UserInfo user_info;

  user_info.owner_id_ = owner_id;

  char buf[3*1<<20];

  impl.set_kv_rs_addr(krs_addr);

  int64_t ret = impl.initialize(rc_addr, app_key, app_ip);

  if (TFS_SUCCESS == ret)
  {
    ret = impl.pread_object(bucket_name, object_name, buf, offset, length, &object_meta_info, &customize_info, user_info);
  }

  if (ret > 0)
  {
    printf("return len: %"PRI64_PREFIX"d\n", ret);

    int fd = open(local_file, O_WRONLY|O_CREAT|O_TRUNC, 0644);
    if (fd > 0)
    {
      while (1)
      {
        int64_t wlen = ::write(fd, buf, ret);
        if (wlen != ret)
        {
          TBSYS_LOG(ERROR, "write to local file error, expect len: %d, return len: %d", MAX_READ_SIZE, wlen);
          ret= TFS_ERROR;
        }
        break;
      }

      close(fd);
    }
    else
    {
      printf("open write to local file: %s fail\n", local_file);
      ret = TFS_ERROR;
    }
  }

  ToolUtil::print_info(ret, "get bucket: %s, object: %s, offset: %"PRI64_PREFIX"d, length: %"PRI64_PREFIX"d => %s",
      bucket_name, object_name, offset, length, local_file);

  return ret;
}

//end for test


int cmd_get_object(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  const char* object_name = param[1].c_str();
  const char* local_file = expand_path(const_cast<string&>(param[2]));

  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.get_object(bucket_name, object_name, local_file, user_info);
  }
  ToolUtil::print_info(ret, "get object: %s, object: %s => %s",
      bucket_name, object_name, local_file);

  return ret;
}

int cmd_del_object(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  const char* object_name = param[1].c_str();
  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.del_object(bucket_name, object_name, user_info);
  }
  ToolUtil::print_info(ret, "del bucket: %s, object: %s", bucket_name, object_name);

  return ret;
}

int cmd_head_object(const VSTRING& param)
{
  const char* bucket_name = param[0].c_str();
  const char* object_name = param[1].c_str();

  ObjectInfo object_info;
  UserInfo user_info;

  RcClientImpl impl;
  impl.set_kv_rs_addr(krs_addr);
  int ret = impl.initialize(rc_addr, app_key, app_ip);

  if (TFS_SUCCESS != ret)
  {
    TBSYS_LOG(DEBUG, "rc client init failed, ret: %d", ret);
  }
  else
  {
    ret = impl.head_object(bucket_name, object_name, &object_info, user_info);
  }

  if (TFS_SUCCESS == ret)
  {
    if (object_info.has_customize_info_)
    {
      CustomizeInfo customize_info = object_info.customize_info_;
      if (customize_info.meta_data_.size() > 0)
      {
        MAP_STRING_ITER iter = customize_info.meta_data_.begin();
        for (; iter != customize_info.meta_data_.end(); iter++)
        {
          printf("key: %s, value: %s\n", iter->first.c_str(), iter->second.c_str());
        }
      }
    }

    printf("create_time: %"PRI64_PREFIX"d, modify_time: %"PRI64_PREFIX"d, total_size: %"PRI64_PREFIX"d, owner_id: %"PRI64_PREFIX"d \n",
        object_info.meta_info_.create_time_, object_info.meta_info_.modify_time_, object_info.meta_info_.big_file_size_, object_info.meta_info_.owner_id_);
  }
  ToolUtil::print_info(ret, "head bucket: %s, object: %s", bucket_name, object_name);

  return ret;
}

