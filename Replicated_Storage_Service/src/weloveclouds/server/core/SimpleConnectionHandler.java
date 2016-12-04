package weloveclouds.server.core;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.networking.IConnectionHandler;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.networking.requests.ICallbackRegister;
import weloveclouds.commons.networking.requests.IExecutable;
import weloveclouds.commons.networking.requests.IRequestFactory;
import weloveclouds.commons.networking.requests.IValidatable;
import weloveclouds.commons.networking.requests.exceptions.IllegalRequestException;

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
public class SimpleConnectionHandler<M, R extends IExecutable<M> & IValidatable<R>> extends Thread
        implements IConnectionHandler, ICallbackRegister {

    private static final Logger LOGGER = Logger.getLogger(SimpleConnectionHandler.class);

    private IConcurrentCommunicationApi communicationApi;
    private IRequestFactory<M, R> requestFactory;
    private Connection connection;
    private IMessageSerializer<SerializedMessage, M> messageSerializer;
    private IMessageDeserializer<M, SerializedMessage> messageDeserializer;

    private List<Runnable> callbacks;
    private ConnectionHandlerShutdownHook shutdownHook;

    protected SimpleConnectionHandler(Builder<M, R> simpleConnectionBuilder) {
        this.communicationApi = simpleConnectionBuilder.communicationApi;
        this.connection = simpleConnectionBuilder.connection;
        this.requestFactory = simpleConnectionBuilder.requestFactory;
        this.messageSerializer = simpleConnectionBuilder.messageSerializer;
        this.messageDeserializer = simpleConnectionBuilder.messageDeserializer;
        this.callbacks = new ArrayList<>();
        registerShutdownHookForConnection();
    }

    @Override
    public void handleConnection() {
        start();
    }


    @Override
    public void registerCallback(Runnable callback) {
        callbacks.add(callback);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        LOGGER.info("Client is connected to server.");
        try {
            while (connection.isConnected()) {
                M response = null;
                try {
                    M receivedMessage = messageDeserializer
                            .deserialize(communicationApi.receiveFrom(connection));
                    LOGGER.debug(CustomStringJoiner.join(" ", "Message received:",
                            receivedMessage.toString()));

                    response =
                            requestFactory.createRequestFromReceivedMessage(receivedMessage, this)
                                    .validate().execute();
                } catch (IllegalRequestException ex) {
                    try {
                        response = (M) ex.getResponse();
                    } catch (ClassCastException e) {
                        LOGGER.error(e);
                    }
                } finally {
                    if (response != null) {
                        try {
                            communicationApi.send(messageSerializer.serialize(response).getBytes(),
                                    connection);
                            LOGGER.debug(CustomStringJoiner.join(" ", "Sent response:",
                                    response.toString()));
                        } catch (IOException e) {
                            LOGGER.error(e);
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
            LOGGER.error(e);
            closeConnection();
        }

        LOGGER.info("Client is disconnected.");
    }

    /**
     * Registers a shutdown hook that will close the connection upon JVM exit.
     */
    private void registerShutdownHookForConnection() {
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } else {
            shutdownHook = new ConnectionHandlerShutdownHook(connection);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    /**
     * Closes the stored connection.
     */
    private void closeConnection() {
        try {
            connection.kill();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * A shutdown hook which closes connection if it was not closed beforehand.
     * 
     * @author Benedek
     */
    private static class ConnectionHandlerShutdownHook extends Thread {

        private static final Logger LOGGER = Logger.getLogger(ConnectionHandlerShutdownHook.class);
        private Connection connection;

        public ConnectionHandlerShutdownHook(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                connection.kill();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * A builder to create a {@link SimpleConnectionHandler} instance.
     * 
     * @author Benoit
     */
    public static class Builder<M, R extends IExecutable<M> & IValidatable<R>> {
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
