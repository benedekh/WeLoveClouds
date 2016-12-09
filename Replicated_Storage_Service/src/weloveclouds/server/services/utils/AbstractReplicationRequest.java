package weloveclouds.server.services.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * An abstract class which represents a replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 *
 * @param <T> The type of the payload that is transferred in the {@link ITransferMessage}.
 */
public abstract class AbstractReplicationRequest<T> implements Runnable {

    private IConcurrentCommunicationApi communicationApi;
    private Connection connection;
    private T payload;

    private Logger logger;

    protected IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer;
    protected IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer;

    protected AbstractReplicationRequest(IConcurrentCommunicationApi communicationApi,
            Connection connection, T payload, Logger logger,
            IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer) {
        this.connection = connection;
        this.communicationApi = communicationApi;
        this.payload = payload;
        this.logger = logger;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
    }

    @Override
    public void run() {
        logger.debug(CustomStringJoiner.join(" ", "Starting replicating (", payload.toString(),
                ") on", connection.toString()));

        try (Connection conn = connection) {
            KVTransferMessage transferMessage = createTransferMessageFrom(payload);
            SerializedMessage serializedMessage = messageSerializer.serialize(transferMessage);

            byte[] response =
                    communicationApi.sendAndExpectForResponse(serializedMessage.getBytes(), conn);
            KVTransferMessage responseMessage = messageDeserializer.deserialize(response);
            if (responseMessage.getStatus() == StatusType.RESPONSE_ERROR) {
                throw new IOException(responseMessage.getResponseMessage());
            }
        } catch (Exception ex) {
            logger.error(CustomStringJoiner.join(" ", "Exception (", ex.toString(),
                    ") occured while replicating on", connection.toString()));
        }

        logger.debug(CustomStringJoiner.join(" ", "Replicating (", payload.toString(), ") on",
                connection.toString(), " finished"));
    }

    /**
     * @return a {@link KVTransferMessage} whose content is the referred payload
     */
    protected abstract KVTransferMessage createTransferMessageFrom(T payload);
}
