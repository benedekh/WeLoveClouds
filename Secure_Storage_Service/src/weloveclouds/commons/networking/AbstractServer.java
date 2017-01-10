package weloveclouds.commons.networking;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.status.ServerStatus;
import weloveclouds.communication.CommunicationApiFactory;

import static weloveclouds.commons.status.ServerStatus.HALTED;

/**
 * Represents an abstract server which can accept connections on the referred port.
 *
 * @author Benoit
 */
public abstract class AbstractServer<M> extends Thread {
    protected Logger logger;
    protected CommunicationApiFactory communicationApiFactory;
    protected ServerStatus status;
    protected ServerSocketFactory serverSocketFactory;
    protected ServerSocket serverSocket;
    protected IMessageSerializer<SerializedMessage, M> messageSerializer;
    protected IMessageDeserializer<M, SerializedMessage> messageDeserializer;
    protected int port;

    private ServerShutdownHook shutdownHook;

    protected AbstractServer(CommunicationApiFactory communicationApiFactory,
                          ServerSocketFactory serverSocketFactory,
                          IMessageSerializer<SerializedMessage, M> messageSerializer,
                          IMessageDeserializer<M, SerializedMessage> messageDeserializer, int
                                  port) throws IOException {
        this.communicationApiFactory = communicationApiFactory;
        this.serverSocketFactory = serverSocketFactory;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
        this.port = port;
        this.serverSocket = serverSocketFactory.createServerSocketFromPort(port);
        this.status = HALTED;
    }

    /**
     * Registers a shutdown hook that will close the server socket upon JVM exit.
     */
    protected void registerShutdownHookForSocket(ServerSocket socket) {
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

        private final Logger LOGGER = Logger.getLogger(ServerShutdownHook.class);
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
}
