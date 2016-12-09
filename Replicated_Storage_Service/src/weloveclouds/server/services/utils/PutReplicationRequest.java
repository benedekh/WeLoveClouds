package weloveclouds.server.services.utils;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * Represents a PUT replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 */
public class PutReplicationRequest extends AbstractReplicationRequest<KVEntry> {

    private static final Logger LOGGER = Logger.getLogger(PutReplicationRequest.class);

    public PutReplicationRequest(IConcurrentCommunicationApi communicationApi,
            Connection connection, KVEntry payload,
            IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer) {
        super(communicationApi, connection, payload, LOGGER, messageSerializer,
                messageDeserializer);
    }

    @Override
    protected KVTransferMessage createTransferMessageFrom(KVEntry payload) {
        return new KVTransferMessage.Builder().putableEntry(payload).status(StatusType.PUT_ENTRY)
                .build();
    }

}
