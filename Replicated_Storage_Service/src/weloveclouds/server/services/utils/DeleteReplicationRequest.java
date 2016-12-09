package weloveclouds.server.services.utils;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * Represents a DELETE replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 */
public class DeleteReplicationRequest extends AbstractReplicationRequest<String> {

    private static final Logger LOGGER = Logger.getLogger(DeleteReplicationRequest.class);

    protected DeleteReplicationRequest(IConcurrentCommunicationApi communicationApi,
            Connection connection, String payload,
            IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer) {
        super(communicationApi, connection, payload, LOGGER, messageSerializer,
                messageDeserializer);
    }

    @Override
    protected KVTransferMessage createTransferMessageFrom(String payload) {
        return new KVTransferMessage.Builder().removableKey(payload)
                .status(StatusType.REMOVE_ENTRY_BY_KEY).build();
    }

}
