package weloveclouds.server.core;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.server.models.RequestFactory;
import weloveclouds.server.models.ResponseFactory;
import weloveclouds.server.parsers.IMessageParser;

import static weloveclouds.server.core.ServerStatus.*;

/**
 * Created by Benoit on 2016-10-29.
 */
public class Server extends Thread {
    private ServerStatus status;
    private IConcurrentCommunicationApi communicationApi;
    private ServerSocketFactory serverSocketFactory;
    private RequestFactory requestFactory;
    private ResponseFactory responseFactory;
    private ServerSocket serverSocket;
    private IMessageParser messageParser;

    private Server(ServerBuilder serverBuilder) throws IOException {
        this.communicationApi = serverBuilder.communicationApi;
        this.serverSocketFactory = serverBuilder.serverSocketFactory;
        this.requestFactory = serverBuilder.requestFactory;
        this.responseFactory = serverBuilder.responseFactory;
        this.serverSocket = serverSocketFactory.createServerSocketFromPort(serverBuilder.port);
        this.messageParser = serverBuilder.messageParser;
    }

    public ServerStatus getStatus() {
        return status;
    }

    @Override
    public void run() {
        status = RUNNING;
        while (status == RUNNING) {
            try {
                new SimpleConnectionHandler.SimpleConnectionBuilder()
                        .connection(new Connection.ConnectionBuilder().socket(serverSocket.accept
                                ()).build())
                        .messageParser(messageParser)
                        .requestFactory(requestFactory)
                        .responseFactory(responseFactory)
                        .communicationApi(communicationApi)
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
        private ResponseFactory responseFactory;
        private IMessageParser messageParser;
        private int port;
        private int cacheSize;

        public ServerBuilder serverSocketFactory(ServerSocketFactory serverSocketFactory) {
            this.serverSocketFactory = serverSocketFactory;
            return this;
        }

        public ServerBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerBuilder messageParser(IMessageParser messageParser) {
            this.messageParser = messageParser;
            return this;
        }

        public ServerBuilder requestFactory(RequestFactory requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public ServerBuilder responseFactory(ResponseFactory responseFactory) {
            this.responseFactory = responseFactory;
            return this;
        }

        public ServerBuilder communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public ServerBuilder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Server build() throws IOException {
            return new Server(this);
        }
    }
}
