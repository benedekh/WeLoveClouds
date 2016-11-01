package weloveclouds.communication.services;


import java.net.Socket;

import weloveclouds.communication.models.Connection;

/**
 * Created by Benoit on 2016-10-31.
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    @Override
    synchronized public boolean isConnected(Connection connection) {
        return false;
    }

    @Override
    synchronized public void send(byte[] message, Connection connection) {

    }

    @Override
    synchronized public byte[] receiveFrom(Connection connection) {
        return new byte[0];
    }
}
