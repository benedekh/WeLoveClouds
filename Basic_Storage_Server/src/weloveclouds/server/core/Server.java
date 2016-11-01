package weloveclouds.server.core;

import static weloveclouds.server.core.ServerStatus.RUNNING;

import java.io.IOException;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;
import weloveclouds.server.models.requests.RequestFactory;

/**
 * Created by Benoit on 2016-10-29.
 */
public class Server extends AbstractServer {
    private CommunicationApiFactory communicationApiFactory;
    private RequestFactory requestFactory;
    private IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer;

    private Server(ServerBuilder serverBuilder) throws IOException {
        super(serverBuilder.serverSocketFactory, serverBuilder.port);
        this.communicationApiFactory = serverBuilder.communicationApiFactory;
        this.requestFactory = serverBuilder.requestFactory;
        this.messageSerializer = serverBuilder.messageSerializer;
        this.messageDeserializer = serverBuilder.messageDeserializer;
    }

    @Override
    public void run() {
        status = RUNNING;
        while (status == RUNNING) {
            try {
                new SimpleConnectionHandler.SimpleConnectionBuilder()
                        .connection(new Connection.ConnectionBuilder().socket(serverSocket.accept())
                                .build())
                        .requestFactory(requestFactory)
                        .communicationApi(communicationApiFactory.createConcurrentCommunicationApiV1())
                        .messageSerializer(messageSerializer)
                        .messageDeserializer(messageDeserializer).build().handleConnection();
            } catch (IOException e) {
                // Log what's going on
            }
        }
    }

    public static class ServerBuilder {
        private CommunicationApiFactory communicationApiFactory;
        private ServerSocketFactory serverSocketFactory;
        private RequestFactory requestFactory;
        private IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer;
        private IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer;
        private int port;

        public ServerBuilder serverSocketFactory(ServerSocketFactory serverSocketFactory) {
            this.serverSocketFactory = serverSocketFactory;
            return this;
        }

        public ServerBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerBuilder requestFactory(RequestFactory requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public ServerBuilder communicationApiFactory(CommunicationApiFactory communicationApiFactory) {
            this.communicationApiFactory = communicationApiFactory;
            return this;
        }

        public ServerBuilder messageSerializer(
                IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public ServerBuilder messageDeserializer(
                IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public Server build() throws IOException {
            return new Server(this);
        }
    }
}
