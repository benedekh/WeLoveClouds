package weloveclouds.commons.exceptions;

/**
 * Represents a client side exception.
 * 
 * @author Benoit
 */
public class ClientSideException extends Exception {
    private static final long serialVersionUID = 7333053297377130794L;

    public ClientSideException(String message) {
        super(message);
    }

    public ClientSideException(String message, Exception cause) {
        super(message, cause);
    }
}
