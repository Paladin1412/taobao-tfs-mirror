AM_CPPFLAGS=-I$(top_srcdir)/src\
	    -I$(TBLIB_ROOT)/include/tbsys\
	    -I$(TBLIB_ROOT)/include/tbnet\
      $(READLINE_INCLUDE)\
		  -I$(TREST_ROOT)/include

AM_LDFLAGS=-lrt -lpthread -ldl $(READLINE_LIB) -lsnappy -ljson -lcurl

bin_PROGRAMS = transfer split_item_tool block_toolv2 transfer_from_filesystem
LDADD = $(top_builddir)/src/tools/util/libtfstoolsutil.a\
	$(top_builddir)/src/requester/libtfssyncutil.a\
  $(top_builddir)/src/clientv2/.libs/libtfsclientv2.a\
	$(top_builddir)/src/new_client/.libs/libtfsclient.a\
  $(top_builddir)/src/requester/libtfsrequester.a\
	$(top_builddir)/src/message/libtfsmessage.a\
	$(top_builddir)/src/common/libtfscommon.a\
	$(TAIR_CACHE_LDFLAGS) \
	$(TBLIB_ROOT)/lib/libtbnet.a\
	$(TBLIB_ROOT)/lib/libtbsys.a\
	$(TREST_ROOT)/lib/libtfs_cpp_restful_client.a

transfer_SOURCES=transfer.cpp transfer.h

split_item_tool_SOURCES=split_item_tool.cpp

block_toolv2_SOURCES=block_tool_v2.cpp

transfer_from_filesystem_SOURCES = transfer_from_filesystem.cpp

# calc_ip_addr_SOURCES=calc_ip_addr.cpp
