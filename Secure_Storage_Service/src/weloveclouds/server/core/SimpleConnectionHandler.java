package weloveclouds.server.core;


import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.networking.AbstractConnectionHandler;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.networking.models.requests.IValidatable;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.server.monitoring.heartbeat.ServiceHealthMonitor;

/**
 * A handler for a client connected to the {@link Server}. It receives and interprets different
 * message from the client over the network, and forwards the clients' requests to the data access
 * layer.
 *
 * @param <M> the type of the message the server accepts
 * @param <R> the type of the request which shall be created from M
 * @author Benoit
 */
public class SimpleConnectionHandler<M, R extends IExecutable<M> & IValidatable<R>>
        extends AbstractConnectionHandler<M> {
    private IRequestFactory<M, R> requestFactory;
    private ServiceHealthMonitor serviceHealthMonitor;

    public SimpleConnectionHandler(Builder<M, R> simpleConnectionBuilder) {
        super(simpleConnectionBuilder.communicationApi, simpleConnectionBuilder.connection,
                simpleConnectionBuilder.messageSerializer,
                simpleConnectionBuilder.messageDeserializer);
        this.requestFactory = simpleConnectionBuilder.requestFactory;
        this.logger = Logger.getLogger(this.getClass());
        this.serviceHealthMonitor = simpleConnectionBuilder.serviceHealthMonitor;
    }

    @Override
    public void handleConnection() {
        start();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        logger.info("Client is connected to server.");
        serviceHealthMonitor.incrementConnections();

        try {
            while (connection.isConnected()) {
                M response = null;
                try {
                    M receivedMessage = messageDeserializer
                            .deserialize(communicationApi.receiveFrom(connection));
                    logger.debug(StringUtils.join(" ", "Message received:", receivedMessage));

                    response =
                            requestFactory.createRequestFromReceivedMessage(receivedMessage, this)
                                    .validate().execute();
                } catch (IllegalRequestException ex) {
                    try {
                        response = (M) ex.getResponse();
                    } catch (ClassCastException e) {
                        logger.error(e);
                    }
                } finally {
                    if (response != null) {
                        try {
                            communicationApi.send(messageSerializer.serialize(response).getBytes(),
                                    connection);
                            logger.debug(StringUtils.join(" ", "Sent response:", response));
                        } catch (IOException e) {
                            logger.error(e);
                        }
                    }
                    for (Runnable callback : callbacks) {
                        Thread callbackThread = new Thread(callback);
                        callbackThread.start();
                        callbackThread.join();
                    }
                }
            }
        } catch (Throwable e) {
            logger.error(e);
            closeConnection();
        }

        logger.info("Client is disconnected.");
        serviceHealthMonitor.decrementConnections();
    }

    /**
     * A builder to create a {@link SimpleConnectionHandler} instance.
     *
     * @author Benoit
     */
    public static class Builder<M, R extends IExecutable<M> & IValidatable<R>> {
        private IConcurrentCommunicationApi communicationApi;
        private IRequestFactory<M, R> requestFactory;
        private Connection<?> connection;
        private IMessageSerializer<SerializedMessage, M> messageSerializer;
        private IMessageDeserializer<M, SerializedMessage> messageDeserializer;
        private ServiceHealthMonitor serviceHealthMonitor;

        public Builder<M, R> connection(Connection<?> connection) {
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

        public Builder<M, R> serviceHealthMonitor(ServiceHealthMonitor serviceHealthMonitor) {
            this.serviceHealthMonitor = serviceHealthMonitor;
            return this;
        }

        public SimpleConnectionHandler<M, R> build() {
            return new SimpleConnectionHandler<>(this);
        }
    }

}
