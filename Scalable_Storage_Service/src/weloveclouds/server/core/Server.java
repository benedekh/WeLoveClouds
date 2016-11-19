package weloveclouds.server.core;

import static weloveclouds.server.core.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.requests.RequestFactory;

/**
 * An exact KV server instance which accepts messages over the network and can handle multiple
 * clients concurrently. For further details refer to {@link SimpleConnectionHandler}.
 * 
 * @author Benoit
 */
public class Server extends AbstractServer {
    private CommunicationApiFactory communicationApiFactory;
    private RequestFactory requestFactory;
    private IMessageSerializer<SerializedMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer;

    private ServerShutdownHook shutdownHook;
    private Logger logger;

    private Server(ServerBuilder serverBuilder) throws IOException {
        super(serverBuilder.serverSocketFactory, serverBuilder.port);
        this.communicationApiFactory = serverBuilder.communicationApiFactory;
        this.requestFactory = serverBuilder.requestFactory;
        this.messageSerializer = serverBuilder.messageSerializer;
        this.messageDeserializer = serverBuilder.messageDeserializer;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void run() {
        status = RUNNING;
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                new SimpleConnectionHandler.SimpleConnectionBuilder()
                        .connection(new Connection.Builder().socket(socket.accept()).build())
                        .requestFactory(requestFactory)
                        .communicationApi(
                                communicationApiFactory.createConcurrentCommunicationApiV1())
                        .messageSerializer(messageSerializer)
                        .messageDeserializer(messageDeserializer).build().handleConnection();
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Active server stopped.");
        }
    }

    private void registerShutdownHookForSocket(ServerSocket socket) {
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } else {
            shutdownHook = new ServerShutdownHook(socket);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    /**
     * A shutdown hook which closes the open server socket if it was not closed beforehand.
     * 
     * @author Benedek
     */
    private class ServerShutdownHook extends Thread {

        private ServerSocket socket;
        private Logger logger;

        public ServerShutdownHook(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    /**
     * A builder to create a {@link Server} instance.
     * 
     * @author Benoit
     */
    public static class ServerBuilder {
        private CommunicationApiFactory communicationApiFactory;
        private ServerSocketFactory serverSocketFactory;
        private RequestFactory requestFactory;
        private IMessageSerializer<SerializedMessage, KVMessage> messageSerializer;
        private IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer;
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

        public ServerBuilder communicationApiFactory(
                CommunicationApiFactory communicationApiFactory) {
            this.communicationApiFactory = communicationApiFactory;
            return this;
        }

        public ServerBuilder messageSerializer(
                IMessageSerializer<SerializedMessage, KVMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public ServerBuilder messageDeserializer(
                IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public Server build() throws IOException {
            return new Server(this);
        }
    }
}
