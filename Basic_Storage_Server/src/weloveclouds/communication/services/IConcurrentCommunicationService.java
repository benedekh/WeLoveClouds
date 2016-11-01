package weloveclouds.communication.services;

import java.net.Socket;

/**
 * Created by Benoit on 2016-11-01.
 */
public interface IConcurrentCommunicationService {
    boolean isConnected(Socket endpointSocket);
    void send(byte[] message, Socket endpointSocket);
    byte[] receive(Socket endpointSocket);
}
