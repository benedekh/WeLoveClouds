package weloveclouds.communication.api;

import java.io.IOException;

import weloveclouds.communication.models.Connection;

/**
 * Created by Benoit on 2016-11-01.
 */
public interface IConcurrentCommunicationApi {

    void send(byte[] message, Connection connection) throws IOException;

    byte[] receiveFrom(Connection connection) throws IOException;
}
