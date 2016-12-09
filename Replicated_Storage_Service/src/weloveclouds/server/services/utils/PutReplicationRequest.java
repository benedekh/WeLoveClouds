package weloveclouds.server.services.utils;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

public class PutReplicationRequest extends AbstractReplicationRequest<KVEntry> {

    private static final Logger LOGGER = Logger.getLogger(PutReplicationRequest.class);

    public PutReplicationRequest(ServerConnectionInfo connectionInfo,
            IConcurrentCommunicationApi communicationApi, ConnectionFactory connectionFactory,
            IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer,
            KVEntry payload) {
        super(connectionInfo, communicationApi, connectionFactory, messageSerializer,
                messageDeserializer, payload, LOGGER);
    }

    @Override
    protected KVTransferMessage createTransferMessageFrom(KVEntry payload) {
        return new KVTransferMessage.Builder().putableEntry(payload).status(StatusType.PUT_ENTRY)
                .build();
    }

}
