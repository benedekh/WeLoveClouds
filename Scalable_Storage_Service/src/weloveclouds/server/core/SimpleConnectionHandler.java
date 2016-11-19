package weloveclouds.server.core;


import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.requests.DataServiceRequestFactory;

/**
 * A handler for a client connected to the {@link Server}. It receives and interprets different
 * message from the client over the network, and forwards the clients' requests to the data access
 * layer.
 * 
 * @author Benoit
 */
public class SimpleConnectionHandler extends Thread implements IConnectionHandler {
    private IConcurrentCommunicationApi communicationApi;
    private DataServiceRequestFactory requestFactory;
    private Connection connection;
    private IMessageSerializer<SerializedMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer;

    private Logger logger;

    private SimpleConnectionHandler(SimpleConnectionBuilder simpleConnectionBuilder) {
        this.communicationApi = simpleConnectionBuilder.communicationApi;
        this.connection = simpleConnectionBuilder.connection;
        this.requestFactory = simpleConnectionBuilder.requestFactory;
        this.messageSerializer = simpleConnectionBuilder.messageSerializer;
        this.messageDeserializer = simpleConnectionBuilder.messageDeserializer;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void handleConnection() {
        start();
    }

    @Override
    public void run() {
        logger.info("Client is connected to server.");

        while (connection.isConnected()) {
            try {
                KVMessage receivedMessage =
                        messageDeserializer.deserialize(communicationApi.receiveFrom(connection));
                logger.debug(CustomStringJoiner.join(" ", "Message received:",
                        receivedMessage.toString()));

                KVMessage response =
                        requestFactory.createRequestFromReceivedMessage(receivedMessage).execute();
                communicationApi.send(messageSerializer.serialize(response).getBytes(), connection);

                logger.debug(CustomStringJoiner.join(" ", "Sent response:", response.toString()));
            } catch (IOException | DeserializationException e) {
                logger.error(e);
            } catch (Throwable e) {
                logger.fatal(e);
            }
        }

        logger.info("Client is disconnected.");
    }

    /**
     * A builder to create a {@link SimpleConnectionHandler} instance.
     * 
     * @author Benoit
     */
    public static class SimpleConnectionBuilder {
        private IConcurrentCommunicationApi communicationApi;
        private DataServiceRequestFactory requestFactory;
        private Connection connection;
        private IMessageSerializer<SerializedMessage, KVMessage> messageSerializer;
        private IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer;

        public SimpleConnectionBuilder connection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public SimpleConnectionBuilder requestFactory(DataServiceRequestFactory requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public SimpleConnectionBuilder communicationApi(
                IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public SimpleConnectionBuilder messageSerializer(
                IMessageSerializer<SerializedMessage, KVMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public SimpleConnectionBuilder messageDeserializer(
                IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public SimpleConnectionHandler build() {
            return new SimpleConnectionHandler(this);
        }
    }
}
