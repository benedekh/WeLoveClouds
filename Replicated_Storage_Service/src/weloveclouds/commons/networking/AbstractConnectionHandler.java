package weloveclouds.commons.networking;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * Created by Benoit on 2016-12-05.
 */
public abstract class AbstractConnectionHandler<M>
        extends Thread implements IConnectionHandler {
    protected IConcurrentCommunicationApi communicationApi;
    protected Connection connection;
    protected IMessageSerializer<SerializedMessage, M> messageSerializer;
    protected IMessageDeserializer<M, SerializedMessage> messageDeserializer;
    protected List<Runnable> callbacks;
    protected Logger logger;

    private ConnectionHandlerShutdownHook shutdownHook;

    public AbstractConnectionHandler(IConcurrentCommunicationApi communicationApi, Connection connection,
                                     IMessageSerializer<SerializedMessage, M> messageSerializer,
                                     IMessageDeserializer<M, SerializedMessage> messageDeserializer) {
        this.communicationApi = communicationApi;
        this.connection = connection;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
        this.callbacks = new ArrayList<>();
        registerShutdownHookForConnection();
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
        private Connection connection;

        ConnectionHandlerShutdownHook(Connection connection) {
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

    @Override
    public void registerCallback(Runnable callback) {
        callbacks.add(callback);
    }

    public abstract void run();
}
