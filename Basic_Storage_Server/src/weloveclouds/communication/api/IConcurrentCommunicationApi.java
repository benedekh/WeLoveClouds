package weloveclouds.communication.api;

import weloveclouds.communication.models.Connection;

/**
 * Created by Benoit on 2016-11-01.
 */
public interface IConcurrentCommunicationApi {

    boolean isConnected(Connection connection);

    void send(byte[] message, Connection connection);

    byte[] receive(Connection connection);
}
