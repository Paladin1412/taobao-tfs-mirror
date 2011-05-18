#include "client_manager.h"
#include "error_msg.h"
#include "status_packet.h"
#include <Memory.hpp>

namespace tfs
{
  namespace common 
  {
    NewClientManager::NewClientManager()
      : factory_(NULL), streamer_(NULL), connmgr_(NULL), transport_(NULL), async_callback_entry_(NULL),
        seq_id_(0), initialize_(false),own_transport_(false)
    {

    }

    NewClientManager::~NewClientManager()
    {
      destroy();
    }

    void NewClientManager::destroy()
    {
      tbutil::Mutex::Lock lock(mutex_);
      if (own_transport_)
      {
        transport_->stop();
        transport_->wait();
        tbsys::gDelete(transport_);
      }
      tbsys::gDelete(connmgr_);
      initialize_ = false;
    }


    int NewClientManager::initialize(BasePacketFactory* factory, BasePacketStreamer* streamer,
                   tbnet::Transport* transport, async_callback_func_entry entry,
                   void* args)
    {
      int32_t iret = (NULL != factory  && NULL != streamer) ? TFS_SUCCESS : TFS_ERROR;
      if (TFS_SUCCESS == iret)
      {
        if (!initialize_)
        {
          tbutil::Mutex::Lock lock(mutex_);
          if (!initialize_)
          {
            streamer_ = streamer;
            factory_  = factory;
            async_callback_entry_ = entry;
            args = NULL;
            own_transport_ = NULL == transport;
            if (own_transport_)
            {
              transport_ =  new tbnet::Transport();
              transport_->start();
            }
            else
            {
              transport_ = transport;
            }
            connmgr_ = new tbnet::ConnectionManager(transport_, streamer_, this);
            initialize_ = true;
            new_clients_.clear();
          }
        }
      }
      return iret;
    }

    tbnet::IPacketHandler::HPRetCode NewClientManager::handlePacket(
        tbnet::Packet* packet, void* args)
    {
      bool call_wakeup = NULL != args && initialize_;
      if (call_wakeup)
      {
        bool is_disconntion_packet = (NULL != packet)
                      && (!packet->isRegularPacket()) //disconntion packet
                      && (dynamic_cast<tbnet::ControlPacket*>(packet)->getCommand() == tbnet::ControlPacket::CMD_DISCONN_PACKET);
        call_wakeup = !is_disconntion_packet;
        if (call_wakeup)
        {
          WaitId id = (*(reinterpret_cast<WaitId*>(&args)));
          TBSYS_LOG(DEBUG, "call_wakeup seq_id: %u, send_id: %d, pcode: %d", id.seq_id_, id.send_id_, packet->getPCode());
          handlePacket(id, packet);
        }
      }
      else
      {
        if (NULL != packet)
        {
          if (packet->isRegularPacket())//data packet
          {
            TBSYS_LOG(INFO, "no client waiting this packet.code: %d", packet->getPCode());
            packet->free();
          }
          else
          {
            TBSYS_LOG(DEBUG, "packet pcode: %d is not regular packet, command: %d, discard anyway. "
                "args is NULL, maybe post channel timeout packet ",
                packet->getPCode(), dynamic_cast<tbnet::ControlPacket*>(packet)->getCommand());
          }
        }
      }
      return tbnet::IPacketHandler::FREE_CHANNEL;
    }

    NewClient* NewClientManager::create_client()
    {
      NewClient* client = NULL;
      if (initialize_)
      {
        tbutil::Mutex::Lock lock(mutex_);
        ++seq_id_;
        if (seq_id_ >= MAX_SEQ_ID)
        {
          seq_id_ = 0;
        }
        NEWCLIENT_MAP_ITER iter = new_clients_.find(seq_id_);
        if (iter == new_clients_.end())
        {
          client = malloc_new_client_object(seq_id_);
          if (NULL == client)
          {
            TBSYS_LOG(ERROR, "%s", "create client object fail");
          }
          else
          {
            TBSYS_LOG(DEBUG, "add client id(%u)", seq_id_);
            new_clients_.insert(std::make_pair(seq_id_, client));
          }
        }
        else
        {
          TBSYS_LOG(ERROR, "client id(%u) was existed", seq_id_);
        }
      }
      else
      {
        TBSYS_LOG(ERROR, "%s", "NewClientManager not initilaize");
      }
      return client;
    }

    bool NewClientManager::destroy_client(NewClient* client)
    {
      bool bret = initialize_;
      if (bret)
      {
        bret =  NULL != client;
        if (bret)
        {
          const uint32_t id = client->get_seq_id();
          tbutil::Mutex::Lock lock(mutex_);
          NEWCLIENT_MAP_ITER iter = new_clients_.find(id);
          if (iter != new_clients_.end())
          {
            free_new_client_object(client);
            TBSYS_LOG(DEBUG, "erase client id(%u)", seq_id_);
            new_clients_.erase(iter);
          }
          else
          {
            bret = false;
            TBSYS_LOG(ERROR, "client id(%u) not found", seq_id_);
          }
        }
        else
        {
          TBSYS_LOG(ERROR, "client object is null when call destroy_client function");
        }
      }
      else
      {
        TBSYS_LOG(ERROR, "%s", "NewClientManager not initilaize");
      }
      return bret;
    }


    bool NewClientManager::handlePacket(const WaitId& id, tbnet::Packet* response)
    {
      bool ret = true;
      bool is_callback = false;
      NewClient* client = NULL;
      {
        tbutil::Mutex::Lock lock(mutex_);
        NEWCLIENT_MAP_ITER iter = new_clients_.find(id.seq_id_);
        if (iter == new_clients_.end())
        {
          TBSYS_LOG(INFO, "client not found, id: %u, pcode: %d", id.seq_id_, response->getPCode());
          ret = false;
        }
        else
        {
          client = iter->second;
          // if got control packet or NULL, we will still add the done counter
          ret = iter->second->handlePacket(id, response, is_callback);
        }
      }

      //async callback
      if (is_callback)
      {
        do_async_callback(client, is_callback);
      }
     
      if (!ret && response != NULL && response->isRegularPacket())
      {
        TBSYS_LOG(DEBUG, "delete response message client id: %u", id.seq_id_);
        response->free();
      }
      return ret;
    }

    tbnet::Packet* NewClientManager::clone_packet(tbnet::Packet* message, const int32_t version, const bool deserialize)
    {
      return factory_->clone_packet(message, version, deserialize);
    }

    NewClient* NewClientManager::malloc_new_client_object(const uint32_t seq_id)
    {
      return new (std::nothrow)NewClient(seq_id);
    }

    void NewClientManager::free_new_client_object(NewClient* client)
    {
      tbsys::gDelete(client);
    }

    bool NewClientManager::do_async_callback(NewClient* client, const bool is_callback)
    {
      bool bret = NULL != client;
      if (bret)
      {
        if (is_callback)
        {
          tbutil::Mutex::Lock lock(mutex_);
          NEWCLIENT_MAP_ITER iter = new_clients_.find(client->get_seq_id());
          if (iter == new_clients_.end())
          {
            TBSYS_LOG(WARN, "'new client object' not found in new_clinet maps by seq_id: %u", client->get_seq_id());
          }
          else
          {
            new_clients_.erase(iter);
          }
          if ( NULL == async_callback_entry_)
          {
            TBSYS_LOG(WARN, "not set async callback function, we'll delete this NewClient object, seq_id: %u",
                seq_id_);
            free_new_client_object(client);
          }
          else
          {
            int32_t iret = async_callback_entry_(client, args_);
            if (TFS_SUCCESS != iret)
            {
              //if have error occur, we'll must be delete client object
              TBSYS_LOG(ERROR, "async callback error, iret: %d", iret);
              free_new_client_object(client);
            }
          }
        }
      }
      return bret;
    }
  }
}
