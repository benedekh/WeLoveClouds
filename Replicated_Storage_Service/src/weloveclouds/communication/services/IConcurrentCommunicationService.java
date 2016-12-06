package weloveclouds.communication.services;

import java.io.IOException;

import weloveclouds.communication.models.Connection;

/**
 * Common interface for a concurrent communication service.
 * 
 * @author Benoit
 */
public interface IConcurrentCommunicationService {
    /**
     * Sends a content over the connection.
     *
     * @throws IOException if any error occurs during the send
     */
    void send(byte[] message, Connection connection) throws IOException;

    /**
     * Sends a message as a byte array to the server. Retries the send as long as it does not
     * receive a response.
     * 
     * @return the response byte array
     * @throws IOException if any error occurs
     */
    byte[] sendAndExpectForResponse(byte[] content, Connection connection) throws IOException;

    /**
     * Receives data over the connection.
     *
     * @throws IOException if any error occurs during the receiveFrom
     */
    byte[] receiveFrom(Connection connection) throws IOException;
}
