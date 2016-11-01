package weloveclouds.communication.services;

import java.io.IOException;

import weloveclouds.communication.exceptions.AlreadyConnectedException;
import weloveclouds.communication.exceptions.AlreadyDisconnectedException;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2016-11-01.
 */
public interface ICommunicationService {
    boolean isConnected();

    void connectTo(ServerConnectionInfo remoteServer) throws IOException, AlreadyConnectedException;

    void disconnect() throws IOException, AlreadyDisconnectedException;

    void send(byte[] content) throws IOException, UnableToSendContentToServerException;

    byte[] receive() throws IOException, ClientNotConnectedException;
}
