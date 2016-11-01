package weloveclouds.server.core;


import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.SerializedKVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.server.models.RequestFactory;

/**
 * Created by Benoit on 2016-10-29.
 */
public class SimpleConnectionHandler extends Thread implements IConnectionHandler {
    private IConcurrentCommunicationApi communicationApi;
    private RequestFactory requestFactory;
    private Connection connection;
    private IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer;

    private SimpleConnectionHandler(SimpleConnectionBuilder simpleConnectionBuilder) {
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
        while (connection.isConnected()) {
            try {
                KVMessage receivedMessage = messageDeserializer.deserialize(communicationApi.receiveFrom(connection));
                KVMessage response = requestFactory.createRequestFromReceivedMessage(receivedMessage)
                        .execute();
                communicationApi.send(messageSerializer.serialize(response).getBytes(), connection);
            } catch (DeserializationException e) {

            }
        }
    }


    public static class SimpleConnectionBuilder {
        private IConcurrentCommunicationApi communicationApi;
        private RequestFactory requestFactory;
        private Connection connection;
        private IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer;
        private IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer;

        public SimpleConnectionBuilder connection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public SimpleConnectionBuilder requestFactory(RequestFactory requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public SimpleConnectionBuilder communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public SimpleConnectionBuilder messageSerializer(IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public SimpleConnectionBuilder messageDeserializer(IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public SimpleConnectionHandler build() {
            return new SimpleConnectionHandler(this);
        }
    }
}
