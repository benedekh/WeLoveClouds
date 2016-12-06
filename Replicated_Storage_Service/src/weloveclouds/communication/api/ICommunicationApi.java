package weloveclouds.communication.api;

import java.io.IOException;

import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Common interface for different communication module implementations to communicate with the
 * server.
 *
 * @author Benoit, Benedek
 */
public interface ICommunicationApi {
    /**
     * Return the version of the API implementation.
     */
    double getVersion();

    /**
     * True if the connection is alive (connected).
     */
    boolean isConnected();

    /**
     * Connects to the server using the {@link ServerConnectionInfo} connection information.
     *
     * @throws UnableToConnectException if any error occurs during establishing the connection
     */
    void connectTo(ServerConnectionInfo remoteServer) throws UnableToConnectException;

    /**
     * Disconnects from the server.
     *
     * @throws UnableToDisconnectException if any error occurs during disconnect
     */
    void disconnect() throws UnableToDisconnectException;

    /**
     * Sends a content over the network to the server.
     *
     * @throws UnableToSendContentToServerException if any error occurs during the send
     */
    void send(byte[] content) throws UnableToSendContentToServerException;

    /**
     * Sends a message as a byte array to the server. Retries the send as long as it does not
     * receive a response.
     * 
     * @return the response byte array
     * @throws IOException if any error occurs
     */
    byte[] sendAndExpectForResponse(byte[] content) throws IOException;

    /**
     * Receives data from the server over the network.
     *
     * @throws ClientNotConnectedException if any error occurs during the receiveFrom
     */
    byte[] receive() throws ClientNotConnectedException, ConnectionClosedException;
}
