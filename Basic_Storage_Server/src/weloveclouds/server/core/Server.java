package weloveclouds.server.core;

import java.io.IOException;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.SerializedKVMessage;
import weloveclouds.server.models.RequestFactory;
import weloveclouds.server.services.IDataAccessService;

import static weloveclouds.server.core.ServerStatus.*;

/**
 * Created by Benoit on 2016-10-29.
 */
public class Server extends AbstractServer {
    private IConcurrentCommunicationApi communicationApi;
    private RequestFactory requestFactory;
    private IDataAccessService dataAccessService;
    private IMessageSerializer messageSerializer;
    private IMessageDeserializer messageDeserializer;

    private Server(ServerBuilder serverBuilder) throws IOException {
        super(serverBuilder.serverSocketFactory, serverBuilder.port);
        this.communicationApi = serverBuilder.communicationApi;
        this.requestFactory = serverBuilder.requestFactory;
        this.dataAccessService = serverBuilder.dataAccessService;
        this.messageSerializer = serverBuilder.messageSerializer;
        this.messageDeserializer = serverBuilder.messageDeserializer;
    }

    @Override
    public void run() {
        status = RUNNING;
        while (status == RUNNING) {
            try {
                new SimpleConnectionHandler.SimpleConnectionBuilder()
                        .connection(new Connection.ConnectionBuilder().socket(serverSocket.accept
                                ()).build())
                        .requestFactory(requestFactory)
                        .communicationApi(communicationApi)
                        .messageSerializer(messageSerializer)
                        .messageDeserializer(messageDeserializer)
                        .build()
                        .handleConnection();
            } catch (IOException e) {
                //Log what's going on
            }
        }
    }

    public static class ServerBuilder {
        private IConcurrentCommunicationApi communicationApi;
        private ServerSocketFactory serverSocketFactory;
        private RequestFactory requestFactory;
        private IDataAccessService dataAccessService;
        private IMessageSerializer messageSerializer;
        private IMessageDeserializer messageDeserializer;
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

        public ServerBuilder communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public ServerBuilder dataAccessService(IDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public ServerBuilder messageSerializer(IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public ServerBuilder messageDeserializer(IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public Server build() throws IOException {
            return new Server(this);
        }
    }
}
