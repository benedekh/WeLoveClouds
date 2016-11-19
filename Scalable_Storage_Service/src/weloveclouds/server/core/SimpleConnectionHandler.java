package weloveclouds.server.core;


import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.core.requests.IExecutable;
import weloveclouds.server.core.requests.IRequestFactory;

/**
 * A handler for a client connected to the {@link Server}. It receives and interprets different
 * message from the client over the network, and forwards the clients' requests to the data access
 * layer.
 * 
 * @param <M> the type of the message the server accepts
 * @param <R> the type of the request which shall be created from M
 * 
 * @author Benoit
 */
public class SimpleConnectionHandler<M, R extends IExecutable<M>> extends Thread
        implements IConnectionHandler {

    private static final Logger LOGGER = Logger.getLogger(SimpleConnectionHandler.class);

    private IConcurrentCommunicationApi communicationApi;
    private IRequestFactory<M, R> requestFactory;
    private Connection connection;
    private IMessageSerializer<SerializedMessage, M> messageSerializer;
    private IMessageDeserializer<M, SerializedMessage> messageDeserializer;

    protected SimpleConnectionHandler(Builder<M, R> simpleConnectionBuilder) {
        this.communicationApi = simpleConnectionBuilder.communicationApi;
        this.connection = simpleConnectionBuilder.connection;
        this.requestFactory = simpleConnectionBuilder.requestFactory;
        this.messageSerializer = simpleConnectionBuilder.messageSerializer;
        this.messageDeserializer = simpleConnectionBuilder.messageDeserializer;
    }

    @Override
    public void handleConnection() {
        start();
    }

    @Override
    public void run() {
        LOGGER.info("Client is connected to server.");

        while (connection.isConnected()) {
            try {
                M receivedMessage =
                        messageDeserializer.deserialize(communicationApi.receiveFrom(connection));
                LOGGER.debug(CustomStringJoiner.join(" ", "Message received:",
                        receivedMessage.toString()));

                M response =
                        requestFactory.createRequestFromReceivedMessage(receivedMessage).execute();
                communicationApi.send(messageSerializer.serialize(response).getBytes(), connection);

                LOGGER.debug(CustomStringJoiner.join(" ", "Sent response:", response.toString()));
            } catch (IOException | DeserializationException e) {
                LOGGER.error(e);
            } catch (Throwable e) {
                LOGGER.fatal(e);
            }
        }

        LOGGER.info("Client is disconnected.");
    }

    /**
     * A builder to create a {@link SimpleConnectionHandler} instance.
     * 
     * @author Benoit
     */
    public static class Builder<M, R extends IExecutable<M>> {
        private IConcurrentCommunicationApi communicationApi;
        private IRequestFactory<M, R> requestFactory;
        private Connection connection;
        private IMessageSerializer<SerializedMessage, M> messageSerializer;
        private IMessageDeserializer<M, SerializedMessage> messageDeserializer;

        public Builder<M, R> connection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public Builder<M, R> requestFactory(IRequestFactory<M, R> requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public Builder<M, R> communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder<M, R> messageSerializer(
                IMessageSerializer<SerializedMessage, M> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public Builder<M, R> messageDeserializer(
                IMessageDeserializer<M, SerializedMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public SimpleConnectionHandler<M, R> build() {
            return new SimpleConnectionHandler<M, R>(this);
        }
    }
}
