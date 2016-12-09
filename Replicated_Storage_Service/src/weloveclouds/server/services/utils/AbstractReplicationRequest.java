package weloveclouds.server.services.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

public abstract class AbstractReplicationRequest<T> implements Runnable {

    private Logger logger;

    private ConnectionFactory connectionFactory;
    private IConcurrentCommunicationApi communicationApi;

    private ServerConnectionInfo connectionInfo;
    private T payload;

    protected IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer;
    protected IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer;

    protected AbstractReplicationRequest(ServerConnectionInfo connectionInfo,
            IConcurrentCommunicationApi communicationApi, ConnectionFactory connectionFactory,
            IMessageSerializer<SerializedMessage, KVTransferMessage> messageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> messageDeserializer,
            T payload, Logger logger) {
        this.connectionInfo = connectionInfo;
        this.communicationApi = communicationApi;
        this.connectionFactory = connectionFactory;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
        this.payload = payload;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.debug(CustomStringJoiner.join(" ", "Starting replicating (", payload.toString(),
                ") on", connectionInfo.toString()));

        try (Connection connection = connectionFactory.createConnectionFrom(connectionInfo)) {
            KVTransferMessage transferMessage = createTransferMessageFrom(payload);
            SerializedMessage serializedMessage = messageSerializer.serialize(transferMessage);

            byte[] response = communicationApi
                    .sendAndExpectForResponse(serializedMessage.getBytes(), connection);
            KVTransferMessage responseMessage = messageDeserializer.deserialize(response);
            if (responseMessage.getStatus() == StatusType.RESPONSE_ERROR) {
                throw new IOException(responseMessage.getResponseMessage());
            }
        } catch (Exception ex) {
            logger.error(CustomStringJoiner.join(" ", "Exception (", ex.toString(),
                    ") occured while replicating on", connectionInfo.toString()));
        }

        logger.debug(CustomStringJoiner.join(" ", "Replicating (", payload.toString(), ") on",
                connectionInfo.toString(), " finished"));
    }

    protected abstract KVTransferMessage createTransferMessageFrom(T payload);
}
