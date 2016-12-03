package weloveclouds.commons.networking;

/**
 * A common abstraction for handling different client connections to the server.
 * 
 * @author Benoit
 */
public interface IConnectionHandler {
    /**
     * To handle the new client connection.
     */
    void handleConnection();
}
