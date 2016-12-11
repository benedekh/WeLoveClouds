package weloveclouds.commons.networking;

import weloveclouds.commons.networking.models.requests.ICallbackRegister;

/**
 * A common abstraction for handling different client connections to the server.
 * 
 * @author Benoit
 */
public interface IConnectionHandler extends ICallbackRegister {
    /**
     * To handle the new client connection.
     */
    void handleConnection();
}
