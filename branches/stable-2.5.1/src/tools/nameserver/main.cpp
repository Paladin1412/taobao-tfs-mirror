/*
 * (C) 2007-2010 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 *
 * Version: $Id: main.cpp 445 2011-06-08 09:27:48Z nayan@taobao.com $
 *
 * Authors:
 *   chuyu <chuyu@taobao.com>
 *      - initial release
 *
 */
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <pthread.h>
#include <vector>
#include <string>
#include <ext/hash_map>
#include <signal.h>
#include <Memory.hpp>
#include "show.h"
#include "metacmp.h"
#include "message/message_factory.h"
#include "common/base_packet_streamer.h"
#include "common/version.h"

using namespace tfs::common;
using namespace tfs::message;
using namespace tfs::tools;
using namespace std;

typedef int (*cmd_function)(VSTRING&);
struct CmdNode
{
  //const char* info_;
  int32_t min_param_count_;
  int32_t max_param_count_;
  cmd_function func_;

  CmdNode()
  {
  }

  CmdNode(int32_t min_param_count, int32_t max_param_count, cmd_function func) :
    min_param_count_(min_param_count), max_param_count_(max_param_count), func_(func)
  {
  }
};

typedef map<string, CmdNode> MSTR_FUNC;
typedef MSTR_FUNC::iterator MSTR_FUNC_ITER;

static const int TFS_CLIENT_QUIT = 0xfff1234;
static const int32_t CMD_MAX_LEN = 4096;
static MSTR_FUNC g_cmd_map;
static char* g_cur_cmd;
static ShowInfo g_show_info;
static CmpInfo g_cmp_info;
static bool g_need_cmp = false;

int main_loop();
int do_cmd(char*);
void print_help();
int cmd_show_help(VSTRING&);
int cmd_quit(VSTRING&);
int cmd_show_block(VSTRING&);
int cmd_check_block(VSTRING&);
int cmd_show_server(VSTRING& param);
int cmd_show_machine(VSTRING& param);
int cmd_show_block_distribution(VSTRING&);
int cmd_show_rack(VSTRING&);
int cmd_show_family(VSTRING&);
int cmd_batch(VSTRING& param);
const char* expand_path(string& path);

typedef map<string, CmdInfo> STR_INT_MAP;
typedef STR_INT_MAP::iterator STR_INT_MAP_ITER;
static STR_INT_MAP g_sub_cmd_map;

#ifdef _WITH_READ_LINE
#include "readline/readline.h"
#include "readline/history.h"
template<class T> const char* get_str(T it)
{
  return it->first.c_str();
}

template<> const char* get_str(VSTRING::iterator it)
{
  return (*it).c_str();
}

template<class T> char* do_match(const char* text, int state, T& m)
{
  static typename T::iterator it;
  static int len = 0;
  const char* cmd = NULL;

  if (!state)
  {
    it = m.begin();
    len = strlen(text);
  }

  while(it != m.end())
  {
    cmd = get_str(it);
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

char* match_cmd(const char* text, int32_t state)
{
  return do_match(text, state, g_cmd_map);
}

char** admin_cmd_completion (const char* text, int, int)
{
  // disable default filename completion
  rl_attempted_completion_over = 1;
  return rl_completion_matches(text, match_cmd);
}

#endif
int get_cmd(char* key, int32_t& cmd, bool& has_value)
{
  STR_INT_MAP_ITER it = g_sub_cmd_map.find(Func::str_to_lower(key));

  if (it == g_sub_cmd_map.end())
  {
    return CMD_UNKNOWN;
  }
  else
  {
    cmd = it->second.cmd_;
    has_value = it->second.has_value_;
    return TFS_SUCCESS;
  }
}
int get_value(const char* data, string& value)
{
  return ((value = data) != "");
}
int get_value(const char* data, uint64_t& value)
{
  int base = 10;
  char* endptr = NULL;

  errno = 0;    // To distinguish success/failure after call
  value = (strtoull(data, &endptr, base));
  //TBSYS_LOG(DEBUG, "val: %lu, %s, %s", value, endptr, data);

  // Check for various possible errors
  if ((errno == ERANGE && (value == UINT64_MAX))
      || (errno != 0 && value == 0)) {
    perror("strtol");
    return TFS_ERROR;
  }

  if (endptr == data) {
    fprintf(stderr, "No digits were found\n");
    return TFS_ERROR;
  }

  if (*endptr != '\0')       // Not necessarily an error...
  {
    printf("Further characters after number: %s\n", endptr);
    return TFS_ERROR;
  }
  return TFS_SUCCESS;
}
int parse_param(const VSTRING& param, ComType com_type, ParamInfo& ret_param)
{
  VSTRING::const_iterator iter = param.begin();
  for (; iter != param.end(); iter++)
  {
    int32_t cmd = -1;
    int ret = TFS_SUCCESS;
    bool has_value = false;
    if (TFS_SUCCESS == get_cmd(const_cast<char*>((*iter).c_str()), cmd, has_value))
    {
      if (has_value)
      {
        if ((++iter) == param.end())
        {
          TBSYS_LOG(ERROR, "please input param value...");
          return TFS_ERROR;
        }
      }
      bool is_common = true;
      switch (cmd)
      {
        case CMD_NUM:
          ret = get_value((*iter).c_str(), ret_param.num_);
          if(0 == ret_param.num_ || ret_param.num_ > (uint64_t)MAX_READ_NUM)
          {
            TBSYS_LOG(ERROR, "please input num in [1, %d]", MAX_READ_NUM);
            return TFS_ERROR;
          }
          break;
        case CMD_COUNT:
          ret = get_value((*iter).c_str(), ret_param.count_);
          break;
        case CMD_INTERVAL:
          ret = get_value((*iter).c_str(), ret_param.interval_);
          break;
        case CMD_REDIRECT:
          ret = get_value((*iter).c_str(), ret_param.filename_);
          break;
        default:
          is_common = false;
          break;
      }
      if (is_common) continue;
      if (com_type & SERVER_TYPE)
      {
        switch (cmd)
        {
          case CMD_SERVER_ID:
            if (get_value((*iter).c_str(), ret_param.server_ip_port_) == TFS_SUCCESS)
            {
              if (ret_param.server_ip_port_.find_first_of(":") == string::npos)
              {
                ret = TFS_ERROR;
              }
            }
            break;
          case CMD_BLOCK_LIST:
            ret_param.type_ = SERVER_TYPE_BLOCK_LIST;
            break;
          case CMD_BLOCK_WRITABLE:
            ret_param.type_ = SERVER_TYPE_BLOCK_WRITABLE;
            break;
          case CMD_BLOCK_MASTER:
            ret_param.type_ = SERVER_TYPE_BLOCK_MASTER;
            break;
          case CMD_NEED_FAMILY:
            ret_param.need_family_ = true;
            break;
          default:
            ret = CMD_UNKNOWN;
            break;
        }
      }
      if (com_type & BLOCK_TYPE)
      {
        uint64_t tmp = 0;
        switch (cmd)
        {
          case CMD_BLOCK_ID:
            if ((ret = get_value((*iter).c_str(), tmp)) == TFS_SUCCESS)
            {
              ret_param.block_id_ = tmp;
            }
            break;
          case CMD_SERVER_LIST:
            g_need_cmp ? (ret_param.type_ = BLOCK_CMP_SERVER) : (ret_param.type_ = BLOCK_TYPE_SERVER_LIST);
            break;
          case CMD_BLOCK_STATUS:
            ret_param.type_ = BLOCK_TYPE_BLOCK_STATUS;
            break;
          case CMD_BLOCK_FULL:
            ret_param.type_ |= BLOCK_TYPE_BLOCK_FULL;// additional condition
            break;
          case CMD_ALL:
            ret_param.type_ = BLOCK_CMP_ALL_INFO;
            break;
          case CMD_PART:
            ret_param.type_ = BLOCK_CMP_PART_INFO;
            break;
          default:
            ret = CMD_UNKNOWN;
            break;
        }
      }
      if (com_type & MACHINE_TYPE)
      {
        switch (cmd)
        {
          case CMD_ALL:
            ret_param.type_ = MACHINE_TYPE_ALL;
            break;
          case CMD_PART:
            ret_param.type_ = MACHINE_TYPE_PART;
            break;
          case CMD_FOR_MONITOR:
            ret_param.type_ = MACHINE_TYPE_FOR_MONITOR;
            break;
          case CMD_NEED_FAMILY:
            ret_param.need_family_ = true;
            break;
          default:
            ret = CMD_UNKNOWN;
            break;
        }
      }
      if (com_type & BLOCK_DISTRIBUTION_TYPE)
      {//这里解析子命令,并记录到ret_param里面
        uint64_t tmp = 0;
        switch (cmd)
        {
          case CMD_BLOCK_ID:
            if ((ret = get_value((*iter).c_str(), tmp)) == TFS_SUCCESS)
            {
              ret_param.block_id_ = tmp;
            }
            break;
          case CMD_IP_ID:
            ret_param.type_ = BLOCK_IP_DISTRIBUTION_TYPE;
            break;
          case CMD_IP_MASK_ID:
          if(!get_value((*iter).c_str(), ret_param.rack_ip_mask_))//mask
            {
              ret = TFS_ERROR;
            }
            if(0 == Func::get_addr(ret_param.rack_ip_mask_.c_str()))
            {
              ret = TFS_ERROR;
            }
            ret_param.type_ = BLOCK_RACK_DISTRIBUTION_TYPE;
            break;
          default:
            ret = CMD_UNKNOWN;
            break;
        }
      }
      if (com_type & RACK_BLOCK_TYPE)
      {
        switch (cmd)
        {
          case CMD_IP_GROUP_ID:
            if (get_value((*iter).c_str(), ret_param.server_ip_port_) )
            {
              string::size_type index = ret_param.server_ip_port_.find_first_of(":");//允许用户输入含端口的ip_group
              if(string::npos != index)
              {
                ret_param.server_ip_port_ = ret_param.server_ip_port_.substr(0, index);
              }
              ret_param.type_ = RACK_BLOCK_TYPE_BLOCK_LIST;//默认为RACK_BLOCK_TYPE_RACK_LIST
            }
            if(0 == Func::get_addr(ret_param.server_ip_port_.c_str()))
            {
              ret = TFS_ERROR;
            }
            break;
          case CMD_IP_MASK_ID:
            if(!get_value((*iter).c_str(), ret_param.rack_ip_mask_))//mask
            {
              ret = TFS_ERROR;
            }
            if(0 == Func::get_addr(ret_param.rack_ip_mask_.c_str()))
            {
              ret = TFS_ERROR;
            }
            break;
          default:
            ret = CMD_UNKNOWN;
            break;
        }
      }
      if (com_type & FAMILY_TYPE)
      {
        uint64_t tmp = 0;
        switch (cmd)
        {
          case CMD_BLOCK_ID://CMD_FAMILY_ID:
            if ((ret = get_value((*iter).c_str(), tmp)) == TFS_SUCCESS)
            {
              ret_param.family_id_ = tmp;
            }
            break;
          case CMD_SERVER_LIST:
            ret_param.type_ = BLOCK_TYPE_SERVER_LIST;
            break;
          default:
            ret = CMD_UNKNOWN;
            break;
        }
      }
    }
    else
    {
      TBSYS_LOG(ERROR, "unknown param......");
      ret = CMD_UNKNOWN;
    }
    if (ret != TFS_SUCCESS)
    {
      TBSYS_LOG(ERROR, "unvalid value...");
      print_help();
      return ret;
    }
  }
  return TFS_SUCCESS;
}

void init()
{
  g_cmd_map["help"] = CmdNode(0, 0, cmd_show_help);
  g_cmd_map["h"] = CmdNode(0, 0, cmd_show_help);
  g_cmd_map["quit"] = CmdNode(0, 0, cmd_quit);
  g_cmd_map["q"] = CmdNode(0, 0, cmd_quit);
  g_cmd_map["exit"] = CmdNode(0, 0, cmd_quit);
  g_cmd_map["block"] = CmdNode(0, 11, cmd_show_block);
  g_cmd_map["check_block"] = CmdNode(0, 0, cmd_check_block);
  g_cmd_map["server"] = CmdNode(0, 11, cmd_show_server);
  g_cmd_map["machine"] = CmdNode(0, 7, cmd_show_machine);
  g_cmd_map["rack"] = CmdNode(0, 12, cmd_show_rack);
  g_cmd_map["block_dist"] = CmdNode(0, 11, cmd_show_block_distribution);
  g_cmd_map["family"] = CmdNode(0, 10, cmd_show_family);
  g_cmd_map["batch"] = CmdNode(1, 1, cmd_batch);

  g_sub_cmd_map["-num"] = CmdInfo(CMD_NUM, true);
  g_sub_cmd_map["-bid"] = CmdInfo(CMD_BLOCK_ID, true);
  g_sub_cmd_map["-sid"] = CmdInfo(CMD_SERVER_ID, true);
  g_sub_cmd_map["-block"] = CmdInfo(CMD_BLOCK_LIST, false);
  g_sub_cmd_map["-writable"] = CmdInfo(CMD_BLOCK_WRITABLE, false);
  g_sub_cmd_map["-master"] = CmdInfo(CMD_BLOCK_MASTER, false);
  g_sub_cmd_map["-server"] = CmdInfo(CMD_SERVER_LIST, false);
  g_sub_cmd_map["-status"] = CmdInfo(CMD_BLOCK_STATUS, false);
  g_sub_cmd_map["-full"] = CmdInfo(CMD_BLOCK_FULL, false);

  g_sub_cmd_map["-all"] = CmdInfo(CMD_ALL, false);
  g_sub_cmd_map["-part"] = CmdInfo(CMD_PART, false);
  g_sub_cmd_map["-monitor"] = CmdInfo(CMD_FOR_MONITOR, false);
  g_sub_cmd_map["-count"] = CmdInfo(CMD_COUNT, true);
  g_sub_cmd_map["-interval"] = CmdInfo(CMD_INTERVAL, true);

  g_sub_cmd_map["-ip"] = CmdInfo(CMD_IP_ID, false);
  g_sub_cmd_map["-mask"] = CmdInfo(CMD_IP_MASK_ID, true);
  g_sub_cmd_map["-g"] = CmdInfo(CMD_IP_GROUP_ID, true);

  g_sub_cmd_map["-n"] = CmdInfo(CMD_NUM, true);
  g_sub_cmd_map["-d"] = CmdInfo(CMD_BLOCK_ID, true);
  g_sub_cmd_map["-b"] = CmdInfo(CMD_BLOCK_LIST, false);
  g_sub_cmd_map["-r"] = CmdInfo(CMD_SERVER_ID, true);
  g_sub_cmd_map["-w"] = CmdInfo(CMD_BLOCK_WRITABLE, false);
  g_sub_cmd_map["-m"] = CmdInfo(CMD_BLOCK_MASTER, false);
  g_sub_cmd_map["-s"] = CmdInfo(CMD_SERVER_LIST, false);
  g_sub_cmd_map["-t"] = CmdInfo(CMD_BLOCK_STATUS, false);
  g_sub_cmd_map["-u"] = CmdInfo(CMD_BLOCK_FULL, false);
  g_sub_cmd_map["-a"] = CmdInfo(CMD_ALL, false);
  g_sub_cmd_map["-p"] = CmdInfo(CMD_PART, false);
  g_sub_cmd_map["-f"] = CmdInfo(CMD_FOR_MONITOR, false);
  g_sub_cmd_map["-y"] = CmdInfo(CMD_NEED_FAMILY, false);
  g_sub_cmd_map["-c"] = CmdInfo(CMD_COUNT, true);
  g_sub_cmd_map["-i"] = CmdInfo(CMD_INTERVAL, true);
  g_sub_cmd_map[">"] = CmdInfo(CMD_REDIRECT, true);
}
void print_help()
{
  fprintf(stderr, "\nsupported command:\n");
  if (!g_need_cmp)
  {
    fprintf(stderr, "block [-n num] [-d block_id] [-s] [-c] [-i] [> filename]   show block info.\n"
        "  -n the number of one fetch, default 1024, optional.\n"
        "  -d block id, optional.\n"
        "  -u list full block only, optional.\n"
        "  -t print block status, optional.\n"
        "  -s print server list, optional.\n"
        "  -c execute times, default 1, it will always loop execute when it is 0, optional.\n"
        "  -i interval time, default 2, optional.\n"
        "  > redirect to file, optional.\n");
    fprintf(stderr, "family [-n num] [-d family_id [-s]] [-c] [-i] [> filename]   show family info.\n"
        "  -n the number of one fetch, default 1024, optional.\n"
        "  -d family id, optional.\n"
        "  -s print server list, can be used when -d specify family optional.\n"
        "  -c execute times, default 1, it will always loop execute when it is 0, optional.\n"
        "  -i interval time, default 2, optional.\n"
        "  > redirect to file, optional.\n");
    fprintf(stderr, "server [-n num] [-r server_ip] [-b] [-w] [-m] [-c] [-i] [-y] [> filename]  show server info.\n"
        "  -n the number of one fetch, default 1024, optional.\n"
        "  -r server ip string, when parameter -n is invalid.\n"
        "  -b print block list, optional.\n"
        "  -w print writable block list, optional.\n"
        "  -m print master block list, optional.\n"
        "  -c execute times, default 1, it will always loop execute when it is 0, optional.\n"
        "  -y show family count for each server, optional.\n"
        "  -i interval time, optional.\n"
        "  > redirect to file, optional.\n");
    fprintf(stderr, "machine [-a] [-p] [-f] [-c] [-i] [-y] [> filename]   show machine info.\n"
        "  -a print all info, optional.\n"
        "  -p print part of infos, optional.\n"
        "  -f print stat of certain infos, for monitor, optional.\n"
        "  -c execute times, default 1, it will always loop execute when it is 0, optional.\n"
        "  -y show family count for each machine, optional.\n"
        "  -i interval\n"
        "  > redirect to file, optional.\n");
    fprintf(stderr, "block_dist [-n num] [ [-ip [-d block_id]] | [-mask ip_mask] ] [-c] [-i] [> filename]   show unnormal block rack distribution, set rack by mask.\n"
        "  -n the number of one fetch, default 1024, optional.\n"
        "  -ip print unnormal block's ds list, one machine as one rack(equal with '-mask 255.255.255.255'), optional.\n"
        "  -d block id, optional.\n"
        "  -mask print unnormal block's ds list, set rack by ip_mask, optional.\n"
        "  -c execute times, default 1, it will always loop execute when it is 0, optional.\n"
        "  -i interval time, default 2, optional.\n"
        "  > redirect to file, optional.\n");
    fprintf(stderr, "rack [-n num] [-mask ip_mask] [-g ip_group] [-c] [-i] [> filename]   show all blocks by rack.\n"
        "  -n the number of one fetch, default 1024, optional.\n"
        "  -mask divide the machines into racks by ip_mask ,default 255.255.255.240, optional.\n"
        "  -g print block_id list of the ip_group's machine rack only, optional.\n"
        "  -c execute times, default 1, it will always loop execute when it is 0, optional.\n"
        "  -i interval time, default 2, optional.\n"
        "  > redirect to file, optional.\n");
    fprintf(stderr, "check_block [-n num] [> filename]   check whether all blocks's replicates position is really the same whith ns block table.\n"
        "  -n the number of one fetch, default 1024, optional.\n"
        "  > redirect to file, optional.\n");
  }
  else
  {
    fprintf(stderr, "block [-a] [-p] [-s]  compare block info\n"
        "  -a compare block all info\n"
        "  -p compare block all info\n"
        "  -s compare server list.\n"
        "  default compare block all info\n");
    fprintf(stderr, "server [-b] [-w] [-m]  compare server info\n"
        "  -b compare block list \n"
        "  -w compare writable block list \n"
        "  -m compare master block list\n"
        "  default compare server info\n");
  }

  fprintf(stderr, "batch      exec cmd in batch\n");
  fprintf(stderr, "quit(q)      quit\n");
  fprintf(stderr, "exit      exit\n");
  fprintf(stderr, "help(h)      show help info\n");
  fprintf(stderr, "vesion(v)      show version info\n");
}

int cmd_show_help(VSTRING&)
{
  print_help();
  return TFS_SUCCESS;
}

int cmd_quit(VSTRING&)
{
  g_show_info.clean_last_file();
  return TFS_CLIENT_QUIT;
}

int cmd_show_block(VSTRING& param)
{
  int ret = TFS_ERROR;
  int8_t type = CMD_NOP;
  g_need_cmp ? (type = BLOCK_CMP_ALL_INFO) : (type = BLOCK_TYPE_BLOCK_INFO);
  ParamInfo ret_param(type);
  if ((ret = parse_param(param, BLOCK_TYPE, ret_param)) != TFS_ERROR)
  {
    if (!g_need_cmp)
    {
      g_show_info.show_block(ret_param.type_, ret_param.num_, ret_param.block_id_, ret_param.count_, ret_param.interval_, ret_param.filename_);
    }
    else
    {
      g_cmp_info.compare(BLOCK_TYPE, ret_param.type_, ret_param.num_);
    }
  }
  return ret;
}

int cmd_check_block(VSTRING& param)
{
  int ret = TFS_ERROR;
  int8_t type = CMD_NOP;
  ParamInfo ret_param(type);
  if ((ret = parse_param(param, CHECK_BLOCK_TYPE, ret_param)) != TFS_ERROR)
  {
    g_show_info.check_block(ret_param.type_, ret_param.num_, ret_param.filename_);
  }
  return ret;
}

int cmd_show_server(VSTRING& param)
{
  int ret = TFS_ERROR;
  ParamInfo ret_param(SERVER_TYPE_SERVER_INFO);
  if ((ret = parse_param(param, SERVER_TYPE, ret_param)) != TFS_ERROR)
  {
    if (!g_need_cmp)
    {
      g_show_info.show_server(ret_param.type_, ret_param.num_, ret_param.server_ip_port_,
          ret_param.count_, ret_param.interval_, ret_param.need_family_, ret_param.filename_);
    }
    else
    {
      g_cmp_info.compare(SERVER_TYPE, ret_param.type_, ret_param.num_);
    }
  }
  return ret;
}

int cmd_show_machine(VSTRING& param)
{
  int ret = TFS_ERROR;
  ParamInfo ret_param(MACHINE_TYPE_ALL);
  if ((ret = parse_param(param, MACHINE_TYPE, ret_param)) != TFS_ERROR)
  {
    g_show_info.show_machine(ret_param.type_, ret_param.num_, ret_param.count_, ret_param.interval_,
        ret_param.need_family_, ret_param.filename_);
  }
  return ret;
}

int cmd_show_block_distribution(VSTRING& param)
{
  int ret = TFS_ERROR;
  int8_t type = CMD_NOP;
  ParamInfo ret_param(type);
  ret_param.rack_ip_mask_ = "255.255.255.240";
  if ((ret = parse_param(param, BLOCK_DISTRIBUTION_TYPE, ret_param)) != TFS_ERROR)
  {
    if(CMD_NOP == ret_param.type_)// BLOCK_IP_DISTRIBUTION_TYPE 和 BLOCK_RACK_DISTRIBUTION_TYPE至少有一个参数
    {
      print_help();
      fprintf(stderr, "\nnotice: [-ip] or [-mask ip_mask] must be chosen one ...\n");
    }
    else
    {
      g_show_info.show_block_distribution(ret_param.type_, ret_param.rack_ip_mask_, ret_param.num_, ret_param.block_id_, ret_param.count_, ret_param.interval_, ret_param.filename_);
    }
  }
  return ret;
}

int cmd_show_rack(VSTRING& param)
{
  int ret = TFS_ERROR;
  int8_t type = RACK_BLOCK_TYPE_RACK_LIST;//默认只显示rack 及block size
  ParamInfo ret_param(type);
  ret_param.rack_ip_mask_ = "255.255.255.240";
  if ((ret = parse_param(param, RACK_BLOCK_TYPE, ret_param)) != TFS_ERROR)
  {
    g_show_info.show_rack_block(ret_param.type_, ret_param.rack_ip_mask_, ret_param.server_ip_port_, ret_param.num_, ret_param.count_, ret_param.interval_, ret_param.filename_);
  }
  return ret;
}

int cmd_show_family(VSTRING& param)
{
  int ret = TFS_ERROR;
  ParamInfo ret_param;
  if ((ret = parse_param(param, FAMILY_TYPE, ret_param)) != TFS_ERROR)
  {
    g_show_info.show_family(ret_param.type_, ret_param.num_, ret_param.family_id_, ret_param.count_, ret_param.interval_, ret_param.filename_);
  }
  return ret;
}

int cmd_batch(VSTRING& param)
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
        fprintf(stdout, "total: %d, %d errors.\r", count, error_count);
        fflush(stdout);
      }
      if (TFS_CLIENT_QUIT == ret)
      {
        break;
      }
    }
    fprintf(stdout, "total: %d, %d errors.\n\n", count, error_count);
    fclose(fp);
  }
  return TFS_SUCCESS;
}

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

int usage(const char *name)
{
  fprintf(stderr, "\n****************************************************************************** \n");
  fprintf(stderr, "You can both get and compare cluster info by this tool.\n");
  fprintf(stderr, "Usage: \n");
  fprintf(stderr, "  %s -s ns_ip_port [-i exec command] [-v]   show server, block, machine and rack machine info.\n", name);
  fprintf(stderr, "  %s -m master_ip_port -s slave_ip_port [-i exec command] [-v]  compare server, block info.\n", name);
  fprintf(stderr, "****************************************************************************** \n");
  fprintf(stderr, "\n");
  exit(TFS_ERROR);
}

void version(const char* app_name)
{
  fprintf(stderr, "%s %s\n", app_name, Version::get_build_description());
  exit(0);
}

static void sign_handler(int32_t sig)
{
  fprintf(stderr, "showssm sig %d.\n", sig);
  switch (sig)
  {
    case SIGINT:
      if (g_show_info.is_loop_)
      {
        g_show_info.interrupt_ = true;
        g_show_info.is_loop_ = false;
      }
      break;
    case SIGTERM:
      fprintf(stderr, "showssm tool exit.\n");
      g_show_info.clean_last_file();
      exit(TFS_ERROR);
      break;
    default:
      break;
  }
}
inline bool is_whitespace(char c)
{
  return (' ' == c || '\t' == c);
}
inline char* strip_line(char* line)
{
  // trim start postion
  while(is_whitespace(*line))
  {
    line++;
  }

  // trim end postion
  int end_pos = strlen(line);
  while (end_pos && (is_whitespace(line[end_pos-1]) || '\n' == line[end_pos-1] || '\r' == line[end_pos-1]))
  {
    end_pos--;
  }
  line[end_pos] = '\0';

  // merge whitespace
  char new_line[CMD_MAX_LEN];
  snprintf(new_line, CMD_MAX_LEN + 1, "%s", line);

  int j = 0;
  for (int i = 0; i < end_pos; i++)
  {
    if (i+1 <= end_pos && line[i] == ' ' && line[i+1] == ' ')
    {
      continue;
    }
    new_line[j] = line[i];
    j++;
  }
  new_line[j] = '\0';
  snprintf(line, end_pos + 1, "%s", new_line);

  return line;
}

static tfs::common::BasePacketStreamer gstreamer;
static tfs::message::MessageFactory gfactory;

int main(int argc,char** argv)
{
  //TODO readline
  int32_t i;
  string ns_ip_port_1;
  string ns_ip_port_2;
  bool directly = false;
  while ((i = getopt(argc, argv, "s:m:ihv")) != EOF)
  {
    switch (i)
    {
      case 's':
        ns_ip_port_2 = optarg;
        break;
      case 'm':
        ns_ip_port_1 = optarg;
        g_need_cmp = true;
        break;
      case 'i':
        directly = true;
        break;
      case 'v':
        version(argv[0]);
        break;
      case 'h':
      default:
        usage(argv[0]);
    }
  }

  if (ns_ip_port_2.empty() || (g_need_cmp && ns_ip_port_1.empty()))
  {
    fprintf(stderr, "please input nameserver ip and port.\n");
    usage(argv[0]);
  }

  TBSYS_LOGGER.setLogLevel("error");

  init();

  gstreamer.set_packet_factory(&gfactory);
  NewClientManager::get_instance().initialize(&gfactory, &gstreamer);

  if (!g_need_cmp)
  {
    g_show_info.set_ns_ip(ns_ip_port_2);
  }
  else
  {
    g_cmp_info.set_ns_ip(ns_ip_port_1, ns_ip_port_2);
  }

  signal(SIGINT, sign_handler);
  signal(SIGTERM, sign_handler);
  if (optind >= argc)
  {
    main_loop();
  }
  else
  {
    if (directly) // ssm ... -i "cmd"
    {
      for (i = optind; i < argc; i++)
      {
        do_cmd(argv[i]);
      }
      g_show_info.clean_last_file();
    }
    else// exec filename
    {
      VSTRING param;
      for (i = optind; i < argc; i++)
      {
        param.clear();
        param.push_back(argv[i]);
        cmd_batch(param);
      }
    }
  }
}
int main_loop()
{
#ifdef _WITH_READ_LINE
  char* cmd_line = NULL;
  rl_attempted_completion_function = admin_cmd_completion;
#else
  char cmd_line[CMD_MAX_LEN];
#endif
  int ret = TFS_ERROR;
  while (1)
  {
    string tips = "";
    if (!g_need_cmp)
    {
      tips = "show > ";
    }
    else
    {
      tips = "cmp > ";
    }
#ifdef _WITH_READ_LINE
    cmd_line = readline(tips.c_str());
    if (!cmd_line)
#else
    fprintf(stderr, tips.c_str());

    if (NULL == fgets(cmd_line, CMD_MAX_LEN, stdin))
#endif
    {
      break;
    }
    ret = do_cmd(cmd_line);
#ifdef _WITH_READ_LINE

    delete cmd_line;
    cmd_line = NULL;
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
  key = strip_line(key);
  if (!key[0])
  {
    return TFS_SUCCESS;
  }
#ifdef _WITH_READ_LINE
  // not blank line, add to history
  add_history(key);
#endif

  char* token = strchr(key, ' ');
  if (token != NULL)
  {
    *token = '\0';
  }

  MSTR_FUNC_ITER it = g_cmd_map.find(Func::str_to_lower(key));

  if (it == g_cmd_map.end())
  {
    fprintf(stderr, "unknown command. \n");
    return TFS_ERROR;
  }
  // ok this is current command
  g_cur_cmd = key;

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
      break;
    }
    param.push_back(token);
  }

  //check param count
  int32_t min_param_count = g_cmd_map[g_cur_cmd].min_param_count_;
  int32_t max_param_count = g_cmd_map[g_cur_cmd].max_param_count_;
  int32_t param_size = static_cast<int32_t>(param.size());
  if ((param_size < min_param_count) || param_size > max_param_count)
  {
    //fprintf(stderr, "%s\n\n", g_cmd_map[g_cur_cmd].info_);
    fprintf(stderr, "bad param...");
    print_help();
    return TFS_ERROR;
  }

  return it->second.func_(param);
}
