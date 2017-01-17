package weloveclouds.communication.services;

import java.io.IOException;
import java.io.InputStream;

import weloveclouds.communication.exceptions.AlreadyConnectedException;
import weloveclouds.communication.exceptions.AlreadyDisconnectedException;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Common interface for a communication service.
 * 
 * @author Benoit
 */
public interface ICommunicationService {
    /**
     * True if the client is connected to a server.
     */
    boolean isConnected();

    /**
     * Connects to a server described by the connection information stored in the remoteServer
     * parameter.
     *
     * @throws IOException see #initializeConnection
     * @throws AlreadyConnectedException if the client was already conencted to a server
     */
    void connectTo(ServerConnectionInfo remoteServer) throws IOException, AlreadyConnectedException;

    /**
     * Disconnects from the server.
     *
     * @throws IOException see {@link Connection#kill()}
     * @throws AlreadyDisconnectedException if the client was not connected
     */
    void disconnect() throws IOException, AlreadyDisconnectedException;

    /**
     * Sends a message as a byte array to the server.
     *
     * @throws UnableToSendContentToServerException if an error occurs
     */
    void send(byte[] content) throws UnableToSendContentToServerException;

    /**
     * Reads a message as a byte array from the server if any is available.
     *
     * @throws IOException see {@link InputStream#read(byte[]) Connection#getInputStream()}
     * @throws ClientNotConnectedException if the client was not connected to the server
     */
    byte[] receive() throws IOException, ClientNotConnectedException;
}
