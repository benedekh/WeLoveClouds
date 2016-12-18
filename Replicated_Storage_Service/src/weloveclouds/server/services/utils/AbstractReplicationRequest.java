package weloveclouds.server.services.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;

/**
 * An abstract class which represents a replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 *
 * @param <T> The type of the payload that is transferred in the {@link ITransferMessage}.
 */
public abstract class AbstractReplicationRequest<T, E extends AbstractReplicationRequest.Builder<T, E>>
        implements Runnable {

    private IConcurrentCommunicationApi communicationApi;
    private Connection connection;
    private T payload;

    private Logger logger;

    protected IMessageSerializer<SerializedMessage, IKVTransferMessage> messageSerializer;
    protected IMessageDeserializer<IKVTransferMessage, SerializedMessage> messageDeserializer;

    protected AbstractReplicationRequest(Builder<T, E> builder) {
        this.connection = builder.connection;
        this.communicationApi = builder.communicationApi;
        this.payload = builder.payload;
        this.logger = builder.logger;
        this.messageSerializer = builder.messageSerializer;
        this.messageDeserializer = builder.messageDeserializer;
    }

    @Override
    public void run() {
        logger.debug(StringUtils.join(" ", "Starting replicating (", payload, ") on", connection));

        try (Connection conn = connection) {
            KVTransferMessage transferMessage = createTransferMessageFrom(payload);
            SerializedMessage serializedMessage = messageSerializer.serialize(transferMessage);

            byte[] response =
                    communicationApi.sendAndExpectForResponse(serializedMessage.getBytes(), conn);
            IKVTransferMessage responseMessage = messageDeserializer.deserialize(response);
            if (responseMessage.getStatus() == StatusType.RESPONSE_ERROR) {
                throw new IOException(responseMessage.getResponseMessage());
            }
        } catch (Exception ex) {
            logger.error(StringUtils.join(" ", "Exception (", ex, ") occured while replicating on",
                    connection));
        }

        logger.debug(
                StringUtils.join(" ", "Replicating (", payload, ") on", connection, " finished"));
    }

    /**
     * @return a {@link KVTransferMessage} whose content is the referred payload
     */
    protected abstract KVTransferMessage createTransferMessageFrom(T payload);

    /**
     * Builder pattern for creating a {@link AbstractReplicationRequest} instance.
     *
     * @author Benedek
     */
    protected abstract static class Builder<T, E extends Builder<T, E>> {
        protected IConcurrentCommunicationApi communicationApi;
        protected Connection connection;
        protected T payload;
        protected Logger logger;
        protected IMessageSerializer<SerializedMessage, IKVTransferMessage> messageSerializer;
        protected IMessageDeserializer<IKVTransferMessage, SerializedMessage> messageDeserializer;

        public Builder<T, E> communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return getThis();
        }

        public Builder<T, E> connection(Connection connection) {
            this.connection = connection;
            return getThis();
        }

        public Builder<T, E> payload(T payload) {
            this.payload = payload;
            return getThis();
        }

        public Builder<T, E> logger(Logger logger) {
            this.logger = logger;
            return getThis();
        }

        public Builder<T, E> messageSerializer(
                IMessageSerializer<SerializedMessage, IKVTransferMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return getThis();
        }

        public Builder<T, E> messageDeserializer(
                IMessageDeserializer<IKVTransferMessage, SerializedMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return getThis();
        }

        protected abstract E getThis();

        protected abstract AbstractReplicationRequest<T, E> build();
    }
}
