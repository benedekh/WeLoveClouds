package weloveclouds.commons.exceptions;

/**
 * Created by Benoit on 2016-11-17.
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
