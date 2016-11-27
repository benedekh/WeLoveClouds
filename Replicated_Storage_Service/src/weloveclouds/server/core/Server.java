package weloveclouds.server.core;

import static weloveclouds.server.core.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.core.requests.IExecutable;
import weloveclouds.server.core.requests.IRequestFactory;
import weloveclouds.server.core.requests.IValidatable;

/**
 * A Server instance which accepts messages over the network and can handle multiple clients
 * concurrently. For further details refer to {@link SimpleConnectionHandler}.
 * 
 * @param <M> the type of the message the server accepts
 * @param <R> the type of the request which shall be created from M
 * 
 * @author Benoit, Benedek
 */
public class Server<M, R extends IExecutable<M> & IValidatable<R>> extends AbstractServer {

    private static final Logger LOGGER = Logger.getLogger(Server.class);

    private CommunicationApiFactory communicationApiFactory;
    private IRequestFactory<M, R> requestFactory;
    private IMessageSerializer<SerializedMessage, M> messageSerializer;
    private IMessageDeserializer<M, SerializedMessage> messageDeserializer;

    private ServerShutdownHook shutdownHook;

    protected Server(Builder<M, R> serverBuilder) throws IOException {
        super(serverBuilder.serverSocketFactory, serverBuilder.port);
        this.communicationApiFactory = serverBuilder.communicationApiFactory;
        this.requestFactory = serverBuilder.requestFactory;
        this.messageSerializer = serverBuilder.messageSerializer;
        this.messageDeserializer = serverBuilder.messageDeserializer;
    }

    @Override
    public void run() {
        status = RUNNING;
        try (ServerSocket socket = serverSocket) {
            registerShutdownHookForSocket(socket);

            while (status == RUNNING) {
                new SimpleConnectionHandler.Builder<M, R>()
                        .connection(new Connection.Builder().socket(socket.accept()).build())
                        .requestFactory(requestFactory)
                        .communicationApi(
                                communicationApiFactory.createConcurrentCommunicationApiV1())
                        .messageSerializer(messageSerializer)
                        .messageDeserializer(messageDeserializer).build().handleConnection();
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } catch (Throwable ex) {
            LOGGER.fatal(ex);
        } finally {
            LOGGER.info("Active server stopped.");
        }
    }

    /**
     * Registers a shutdown hook that will close the server socket upon JVM exit.
     */
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
    private static class ServerShutdownHook extends Thread {

        private static final Logger LOGGER = Logger.getLogger(ServerShutdownHook.class);
        private ServerSocket socket;

        public ServerShutdownHook(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * A builder to create a {@link Server} instance.
     * 
     * @author Benoit
     */
    public static class Builder<M, R extends IExecutable<M> & IValidatable<R>> {
        private CommunicationApiFactory communicationApiFactory;
        private ServerSocketFactory serverSocketFactory;
        private IRequestFactory<M, R> requestFactory;
        private IMessageSerializer<SerializedMessage, M> messageSerializer;
        private IMessageDeserializer<M, SerializedMessage> messageDeserializer;
        private int port;

        public Builder<M, R> serverSocketFactory(ServerSocketFactory serverSocketFactory) {
            this.serverSocketFactory = serverSocketFactory;
            return this;
        }

        public Builder<M, R> port(int port) {
            this.port = port;
            return this;
        }

        public Builder<M, R> requestFactory(IRequestFactory<M, R> requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public Builder<M, R> communicationApiFactory(
                CommunicationApiFactory communicationApiFactory) {
            this.communicationApiFactory = communicationApiFactory;
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

        public Server<M, R> build() throws IOException {
            return new Server<M, R>(this);
        }
    }
}
