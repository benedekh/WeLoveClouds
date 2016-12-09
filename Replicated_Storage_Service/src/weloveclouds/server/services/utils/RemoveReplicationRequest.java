package weloveclouds.server.services.utils;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

public class RemoveReplicationRequest extends AbstractReplicationRequest<String> {

    private static final Logger LOGGER = Logger.getLogger(RemoveReplicationRequest.class);

    protected RemoveReplicationRequest(ServerConnectionInfo connectionInfo,
            IConcurrentCommunicationApi communicationApi, ConnectionFactory connectionFactory,
            IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer,
            String payload) {
        super(connectionInfo, communicationApi, connectionFactory, messageSerializer,
                messageDeserializer, payload, LOGGER);
    }

    @Override
    protected KVTransferMessage createTransferMessageFrom(String payload) {
        return new KVTransferMessage.Builder().removableKey(payload)
                .status(StatusType.REMOVE_ENTRY_BY_KEY).build();
    }

}
