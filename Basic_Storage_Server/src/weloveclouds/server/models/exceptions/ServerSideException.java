package weloveclouds.server.exceptions;

/**
 * Represents an exception that occurred on the server side.
 * 
 * @author Benedek
 */
public class ServerSideException extends Exception {

    private static final long serialVersionUID = -686060441528416579L;

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(String message, Exception cause) {
        super(message, cause);
    }

}
