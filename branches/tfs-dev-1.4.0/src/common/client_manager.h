#ifndef TFS_COMMON_CLIENT_MANAGER_H_
#define TFS_COMMON_CLIENT_MANAGER_H_

#include <tbnet.h>
#include <Mutex.h>
#include <ext/hash_map>
#include "base_packet.h"
#include "base_packet_factory.h"
#include "base_packet_streamer.h"
#include "new_client.h"
#include "define.h"

namespace tfs
{
  namespace common 
  {
    class NewClientManager : public tbnet::IPacketHandler
    {
        friend int NewClient::post_request(const uint64_t server, tbnet::Packet* packet, uint8_t& send_id);
        friend bool NewClient::async_wait();
        typedef __gnu_cxx::hash_map<uint32_t, NewClient*> NEWCLIENT_MAP;
        typedef NEWCLIENT_MAP::iterator NEWCLIENT_MAP_ITER;
        typedef int (*async_callback_func_entry)(NewClient* client, void* args);
      public:
        NewClientManager();
        virtual ~NewClientManager();
        static NewClientManager& get_instance()
        {
          static NewClientManager client_manager;
          return client_manager;
        }
        int initialize(BasePacketFactory* factory, BasePacketStreamer* streamer,
                      tbnet::Transport* transport = NULL, async_callback_func_entry entry = NULL,
                      void* args = NULL);
        void destroy();
        tbnet::IPacketHandler::HPRetCode handlePacket(tbnet::Packet* packet, void* args);
        NewClient* create_client();
        bool destroy_client(NewClient* client);

        tbnet::Packet* clone_packet(tbnet::Packet* message, const int32_t version = 2, const bool deserialize = false);

      private:
        bool handlePacket(const WaitId& id, tbnet::Packet* response);
        static NewClient* malloc_new_client_object(const uint32_t seq_id);
        static void free_new_client_object(NewClient* client);
        bool do_async_callback(NewClient* client, const bool is_callback);

      private:
        DISALLOW_COPY_AND_ASSIGN(NewClientManager);
        NEWCLIENT_MAP new_clients_;
        BasePacketFactory* factory_;                                                                   
        BasePacketStreamer* streamer_;
        tbnet::ConnectionManager* connmgr_; 
        tbnet::Transport* transport_;

        tbutil::Mutex mutex_;
        async_callback_func_entry async_callback_entry_;
        void* args_;
        static const uint32_t MAX_SEQ_ID = 0xFFFFFF - 1;
        uint32_t seq_id_;

        bool initialize_;
        bool own_transport_;
    };
  }/* message */
}/* tfs */
#endif
