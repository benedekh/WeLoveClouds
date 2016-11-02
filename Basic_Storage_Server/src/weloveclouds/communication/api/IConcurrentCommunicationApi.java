package weloveclouds.communication.api;

import java.io.IOException;

import weloveclouds.communication.models.Connection;

/**
 * Interface for the concurrent communication API.
 * 
 * Benoit
 */
public interface IConcurrentCommunicationApi {

    /**
     * Sends a content over the connection.
     *
     * @throws IOException if any error occurs during the send
     */
   void send(byte[] message, Connection connection) throws IOException;


    /**
     * Receives data over the connection.
     *
     * @throws IOException if any error occurs during the receiveFrom
     */
    byte[] receiveFrom(Connection connection) throws IOException;
}
