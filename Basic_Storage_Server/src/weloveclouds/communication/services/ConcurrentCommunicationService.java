package weloveclouds.communication.services;


import java.net.Socket;

/**
 * Created by Benoit on 2016-10-31.
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    @Override
    synchronized public boolean isConnected(Socket endpointSocket) {
        return false;
    }

    @Override
    synchronized public void send(byte[] message, Socket endpointSocket) {

    }

    @Override
    synchronized public byte[] receive(Socket endpointSocket) {
        return new byte[0];
    }
}
