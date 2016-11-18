package weloveclouds.communication.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.exceptions.AlreadyConnectedException;
import weloveclouds.communication.exceptions.AlreadyDisconnectedException;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * The communication module implementation which executes the network operations (connect,
 * disconnect, send, receiveFrom).
 *
 * @author Benoit, Benedek
 */
public class CommunicationService implements ICommunicationService {

    private static final Logger LOGGER = Logger.getLogger(CommunicationService.class);

    private SocketFactory socketFactory;
    private Connection connectionToEndpoint;
    private Thread connectionShutdownHook;

    /**
     * @param socketFactory a factory to create a socket for connection
     */
    public CommunicationService(SocketFactory socketFactory) {
        this.connectionToEndpoint = new Connection.ConnectionBuilder().build();
        this.socketFactory = socketFactory;
    }

    @Override
    public boolean isConnected() {
        return connectionToEndpoint.isConnected();
    }

    @Override
    public void connectTo(ServerConnectionInfo remoteServer)
            throws IOException, AlreadyConnectedException {
        if (!connectionToEndpoint.isConnected()) {
            try {
                LOGGER.debug("Removing previously registered shutdown hook.");
                if (connectionShutdownHook != null) {
                    Runtime.getRuntime().removeShutdownHook(connectionShutdownHook);
                }
            } catch (IllegalStateException | NullPointerException e) {
                // No hook previously added
                LOGGER.debug(e.getMessage(), e);
            }
            initializeConnection(remoteServer);
            LOGGER.info("Connection established.");
        } else {
            LOGGER.debug("Already connected to a server.");
            throw new AlreadyConnectedException();
        }
    }

    /**
     * See {@link #connectTo(ServerConnectionInfo)}
     *
     * @throws IOException see {@link SocketFactory#createTcpSocketFromInfo(ServerConnectionInfo)}
     */
    private void initializeConnection(ServerConnectionInfo remoteServer) throws IOException {
        LOGGER.debug(CustomStringJoiner.join(" ", "Trying to connect to", remoteServer.toString()));
        connectionToEndpoint = new Connection.ConnectionBuilder().remoteServer(remoteServer)
                .socket(socketFactory.createTcpSocketFromInfo(remoteServer)).build();

        // create shutdown hook to automatically close the connection
        LOGGER.debug("Creating shutdown hook for connection.");
        connectionShutdownHook = new Thread(new ConnectionCloser(connectionToEndpoint));
        LOGGER.debug("Registering shutdown hook to JVM.");
        Runtime.getRuntime().addShutdownHook(connectionShutdownHook);
    }

    @Override
    public void disconnect() throws IOException, AlreadyDisconnectedException {
        if (connectionToEndpoint.isConnected()) {
            LOGGER.debug("Closing the connection.");
            connectionToEndpoint.kill();
            LOGGER.info("Disconnected from the server.");
        } else {
            String message = "The communication service is already disconnected";
            LOGGER.debug(message);
            throw new AlreadyDisconnectedException(message);
        }
    }

    @Override
    public void send(byte[] content) throws IOException, UnableToSendContentToServerException {
        if (connectionToEndpoint.isConnected()) {
            LOGGER.debug("Getting output stream from the connection.");
            OutputStream outputStream = connectionToEndpoint.getOutputStream();
            LOGGER.debug("Sending message over the connection.");
            outputStream.write(content);
            outputStream.flush();
            LOGGER.info("Message sent.");
        } else {
            LOGGER.debug("Client is not connected, so message cannot be sent.");
            throw new ClientNotConnectedException();
        }
    }

    @Override
    public byte[] receive() throws IOException, ClientNotConnectedException {
        if (connectionToEndpoint.isConnected()) {
            byte[] receivedData = null;

            InputStream socketDataReader = connectionToEndpoint.getInputStream();

            while (receivedData == null) {
                int availableBytes = socketDataReader.available();
                if (availableBytes != 0) {
                    LOGGER.debug(CustomStringJoiner.join(" ", "Receiving",
                            String.valueOf(availableBytes), "from the connection."));
                    receivedData = new byte[availableBytes];
                    socketDataReader.read(receivedData);
                    LOGGER.debug("Data received from the network.");
                }
            }
            return receivedData;
        } else {
            LOGGER.debug("Client is not connected, so message cannot be received.");
            throw new ClientNotConnectedException();
        }
    }

    /**
     * A shutdown runnable that closes the connection as soon as the runnable is executed and the
     * connection is open.
     *
     * @author Benedek
     */
    private static class ConnectionCloser implements Runnable {
        private static final Logger LOGGER = Logger.getLogger(ConnectionCloser.class);
        private Connection connection;

        public ConnectionCloser(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            if (connection.isConnected()) {
                try {
                    LOGGER.debug("Closing unclosed connection in the shutdown hook.");
                    connection.kill();
                    LOGGER.debug("Connection is closed in the shutdown hook.");
                } catch (IOException e) {
                    // suppress exception because the thread is invoked as soon as JVM is to be shut
                    // down
                    LOGGER.error(e);
                }
            }
        }

    }
}
