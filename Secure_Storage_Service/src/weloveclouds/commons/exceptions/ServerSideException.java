package weloveclouds.commons.exceptions;

/**
 * Represents a server side exception.
 * 
 * @author Benoit
 */
public class ServerSideException extends Exception {
    private static final long serialVersionUID = 7764943152427779038L;

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(String message, Throwable cause) {
        super(message, cause);
    }
}
