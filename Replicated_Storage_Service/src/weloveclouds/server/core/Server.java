package weloveclouds.server.core;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.Connection;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * A Server instance which accepts messages over the network and can handle multiple clients
 * concurrently. For further details refer to {@link SimpleConnectionHandler}.
 *
 * @param <M> the type of the message the server accepts
 * @param <R> the type of the request which shall be created from M
 * @author Benoit, Benedek
 */
public class Server<M, R extends IExecutable<M> & IValidatable<R>> extends AbstractServer<M> {
    private IRequestFactory<M, R> requestFactory;

    protected Server(Builder<M, R> serverBuilder) throws IOException {
        super(serverBuilder.communicationApiFactory, serverBuilder.serverSocketFactory,
                serverBuilder.messageSerializer, serverBuilder.messageDeserializer,
                serverBuilder.port);
        this.logger = Logger.getLogger(this.getClass());
        this.requestFactory = serverBuilder.requestFactory;
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
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Active server stopped.");
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
            return new Server<>(this);
        }
    }
}
