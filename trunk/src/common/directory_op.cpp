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
 *   duolong <duolong@taobao.com>
 *      - initial release
 *
 */
#include "directory_op.h"
#include <dirent.h>
#include <sys/types.h>
#include <unistd.h>
#include <string.h>
#include <sys/stat.h>
#include <stdio.h>

#include <iostream>
#include <errno.h>

namespace tfs
{
  namespace common
  {
    static char* strip_tail_dir_slashes(char* fname, const int len)
    {
      if (fname == NULL)
        return NULL;

      int i = len - 1;
      if (len > 1)
      {
        while (fname[i] == '/')
        {
          fname[i--] = '\0';
        }
      }
      return fname;
    }

    bool DirectoryOp::exists(const char* filename)
    {
      if (filename == NULL)
        return false;

      int len = strlen(filename);
      if (len > MAX_PATH || len <= 0)
        return false;

      struct stat64 file_info;
      char path[MAX_PATH + 1];
      strncpy(path, filename, len);
      path[len] = '\0';
      char* copy = strip_tail_dir_slashes(path, len);
      int32_t result = stat64(copy, &file_info);

      return (result == 0);
    }

    bool DirectoryOp::delete_file(const char* filename)
    {
      if (filename == NULL)
        return false;

      bool ret = true;
      bool exist = true;

      if (!exists(filename))
        exist = false;

      if (exist)
      {
        if (is_directory(filename))
          ret = false;
      }

      if (ret && exist)
      {
        if (unlink(filename) == -1)
        {
          std::cout << "unlink file: " << filename << " errno: " << errno << std::endl;
          ret = false;
        }
      }
      return !exist ? true : ret;
    }

    bool DirectoryOp::delete_directory_recursively(const char *directory, bool delete_flag)
    {
      if (directory == NULL)
        return false;

      struct dirent dirent;
      struct dirent* result = NULL;
      DIR* dir = NULL;
      bool ret = true;
      dir = opendir(directory);
      if (!dir)
      {
        ret = false;
      }

      while (!readdir_r(dir, &dirent, &result) && result && ret)
      {
        char* name = result->d_name;
        if (strcmp(name, ".") == 0 || strcmp(name, "..") == 0 || strcmp(name, "lost+found") == 0)
        {
          continue;
        }

        char path[MAX_PATH];
        snprintf(path, MAX_PATH, "%s%c%s", directory, '/', name);
        if (is_directory(path))
        {
          if (!delete_directory_recursively(path, true))
          {
            ret = false;
            break;
          }
        }
        else
        {
          if (!delete_file(path))
          {
            ret = false;
            break;
          }
        }
      }

      if (dir != NULL)
        closedir(dir);

      if (!delete_flag)
      {
        return ret;
      }
      else
      {
        return ret ? delete_directory(directory) : ret;
      }
    }

    bool DirectoryOp::is_directory(const char* dirname)
    {
      if (dirname == NULL)
        return false;

      int dir_len = strlen(dirname);
      if (dir_len > MAX_PATH || dir_len <= 0)
        return false;

      struct stat64 file_info;
      char path[MAX_PATH + 1];
      strncpy(path, dirname, dir_len);
      path[dir_len] = '\0';
      char* copy = strip_tail_dir_slashes(path, dir_len);
      int result = stat64(copy, &file_info);

      return result < 0 ? false : S_ISDIR(file_info.st_mode);
    }

    bool DirectoryOp::delete_directory(const char* dirname)
    {
      if (dirname == NULL)
        return false;

      bool ret = true;
      bool isexist = true;
      if (!exists(dirname))
        isexist = false;

      if (isexist)
      {
        if (!is_directory(dirname))
          ret = false;
      }

      if (ret && isexist)
      {
        if (rmdir(dirname) != 0)
          ret = false;
      }
      return !isexist ? true : ret;
    }

    //create the give dirname, return true on success or dirname exists 
    bool DirectoryOp::create_directory(const char* dirname)
    {
      if (dirname == NULL)
        return false;

      mode_t umake_value = umask(0);
      umask(umake_value);
      mode_t mode = (S_IRWXUGO & (~umake_value)) | S_IWUSR | S_IXUSR;
      bool ret = false;
      if (mkdir(dirname, mode) == 0)
        ret = true;

      if (!ret)
      {
        if (errno == EEXIST)
          ret = true;
      }
      return ret;
    }

    //creates the full path of fullpath, return true on success
    bool DirectoryOp::create_full_path(const char* fullpath)
    {
      if (fullpath == NULL)
        return false;

      int32_t iLen = strlen(fullpath);
      if (iLen > MAX_PATH || iLen <= 0x01)
        return false;

      bool ret = true;
      struct stat stats;
      if (lstat(fullpath, &stats) == 0 && S_ISDIR(stats.st_mode))
      {

      }
      else
      {
        char dirpath[MAX_PATH + 1];
        strncpy(dirpath, fullpath, iLen);
        dirpath[iLen] = '\0';

        char *path = dirpath;
        if (ret)
        {
          while (*path == '/')
            path++;
        }
        while (ret)
        {
          path = strchr(path, '/');
          if (path == NULL)
          {
            break;
          }

          *path = '\0';

          if (!is_directory(dirpath))
          {
            if (!create_directory(dirpath))
            {
              ret = false;
              break;
            }
          }
          *path++ = '/';

          while (*path == '/')
          {
            path++;
          }
        }

        if (ret)
        {
          if (!is_directory(dirpath))
          {
            if (!create_directory(dirpath))
              ret = false;
          }
        }
      }
      return ret;
    }

    bool DirectoryOp::rename(const char* srcfilename, const char* destfilename)
    {
      if (srcfilename == NULL || destfilename == NULL)
        return false;

      bool ret = true;
      if (::rename(srcfilename, destfilename) != 0)
        ret = false;
      return ret;
    }

    //return the size of filename 
    int64_t DirectoryOp::get_size(const char *filename)
    {
      if (filename == NULL)
        return 0;

      int64_t ret = 0;
      bool is_exists = true;
      if (!exists(filename))
        is_exists = false;

      if (is_exists)
      {
        if (is_directory(filename))
          is_exists = false;
      }

      if (is_exists)
      {
        struct stat64 file_info;
        if (stat64(filename, &file_info) == 0)
          ret = file_info.st_size;
      }
      return ret;
    }

  }
}
