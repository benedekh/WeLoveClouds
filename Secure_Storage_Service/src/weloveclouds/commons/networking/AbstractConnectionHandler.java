package weloveclouds.commons.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;

/**
 * An abstract connection handler, which accepts and handles connections.
 * 
 * @author Benoit
 */
public abstract class AbstractConnectionHandler<M> extends Thread implements IConnectionHandler {
    protected IConcurrentCommunicationApi communicationApi;
    protected Connection<?> connection;
    protected IMessageSerializer<SerializedMessage, M> messageSerializer;
    protected IMessageDeserializer<M, SerializedMessage> messageDeserializer;
    protected List<Runnable> callbacks;
    protected Logger logger;

    private ConnectionHandlerShutdownHook shutdownHook;

    public AbstractConnectionHandler(IConcurrentCommunicationApi communicationApi,
            Connection<?> connection, IMessageSerializer<SerializedMessage, M> messageSerializer,
            IMessageDeserializer<M, SerializedMessage> messageDeserializer) {
        this.communicationApi = communicationApi;
        this.connection = connection;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
        this.callbacks = new ArrayList<>();
        registerShutdownHookForConnection();
    }

    @Override
    public void registerCallback(Runnable callback) {
        callbacks.add(callback);
    }

    /**
     * Registers a shutdown hook that will close the connection upon JVM exit.
     */
    protected void registerShutdownHookForConnection() {
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
    protected void closeConnection() {
        try {
            connection.kill();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * A shutdown hook which closes connection if it was not closed beforehand.
     *
     * @author Benedek
     */
    private class ConnectionHandlerShutdownHook extends Thread {

        private Logger logger;
        private Connection<?> connection;

        ConnectionHandlerShutdownHook(Connection<?> connection) {
            this.logger = Logger.getLogger(this.getClass());
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                connection.kill();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

}
