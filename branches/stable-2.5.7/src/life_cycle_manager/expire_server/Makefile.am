AM_CPPFLAGS=-Wall -I$(top_srcdir)/src\
            -I$(TBLIB_ROOT)/include/tbsys\
            -I$(TBLIB_ROOT)/include/tbnet \
						  $(MYSQL_CFLAGS)\
						-I$(TREST_ROOT)/include\
						-I$(EASY_ROOT)/include/easy

AM_LDFLAGS=-lz -lrt -lpthread -ldl -luuid -ljson -lcurl -lidn -lssl

LDADD=$(top_builddir)/src/message/libtfsmessage.a \
      $(top_builddir)/src/common/libtfscommon.a\
			$(top_builddir)/src/common/mysql_cluster/libkvengine_mysql.a \
			$(MYSQL_LDFLAGS)\
      $(TBLIB_ROOT)/lib/libtbnet.a \
      $(TBLIB_ROOT)/lib/libtbsys.a \
      $(TREST_ROOT)/lib/libtfs_cpp_restful_client.a \
			$(EASY_ROOT)/lib64/libeasy.a
			
noinst_LIBRARIES=libexpireserver.a

expireserver_INCLUDE=expire_heart_manager.h  expire_service.h  clean_task_helper.h


libexpireserver_a_SOURCES= expire_heart_manager.cpp  expire_service.cpp  clean_task_helper.cpp \
													 $(expireserver_INCLUDE)


bin_PROGRAMS = expireserver 

expireserver_SOURCES=$(libexpireserver_a_SOURCES) main.cpp
#clean_test_SOURCES=clean_test.cpp


