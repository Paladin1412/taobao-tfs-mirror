AM_CPPFLAGS=-I$(top_srcdir)/src\
	    -I$(TBLIB_ROOT)/include/tbsys \
	    -I$(TBLIB_ROOT)/include/tbnet\
	    -fPIC

AM_LDFLAGS=-lrt -lpthread -ldl

noinst_LIBRARIES = libtfsmessage.a
libtfsmessage_a_SOURCES = block_info_message.cpp create_filename_message.cpp\
			  message_factory.cpp client_cmd_message.cpp\
			  dataserver_message.cpp unlink_file_message.cpp\
			  rollback_message.cpp read_data_message.cpp\
			  close_file_message.cpp file_info_message.cpp\
			  rename_file_message.cpp server_status_message.cpp\
			  write_data_message.cpp compact_block_message.cpp\
			  heart_message.cpp replicate_block_message.cpp\
			  status_message.cpp reload_message.cpp admin_cmd_message.cpp\
			  tfs_packet_streamer.cpp client.cpp client_pool.cpp\
			  async_client.cpp server_meta_info_message.cpp\
			  oplog_sync_message.cpp crc_error_message.cpp\
				client_manager.cpp new_client.cpp dump_plan_message.cpp\
			  block_info_message.h client_cmd_message.h create_filename_message.h\
			  message_factory.h client_cmd_message.h\
			  dataserver_message.h unlink_file_message.h\
			  rollback_message.h read_data_message.h\
			  close_file_message.h file_info_message.h\
			  rename_file_message.h server_status_message.h\
			  write_data_message.h compact_block_message.h\
			  heart_message.h replicate_block_message.h\
			  status_message.h reload_message.h admin_cmd_message.h\
			  tfs_packet_streamer.h client.h client_pool.h\
			  async_client.h server_meta_info_message.h\
			  oplog_sync_message.h crc_error_message.h\
				client_manager.h new_client.h\
			  message.h


libtfsmessage_a_CPPFLAGS = $(AM_CPPFLAGS)

#bin_PROGRAMS = test_message
#test_message_SOURCES =  test_message.cpp
#test_message_LDFLAGS = $(AM_LDFLAGS) $(top_builddir)/src/message/libtfsmessage.a \
#											 $(top_builddir)/src/common/libtfscommon.a \
#											 $(TBLIB_ROOT)/lib/libtbnet.a \
#											 $(TBLIB_ROOT)/lib/libtbsys.a 


install:
