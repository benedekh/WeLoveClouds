package weloveclouds.commons.exceptions;

/**
 * Created by Benoit on 2016-11-19.
 */
public class ServerSideException extends Exception {
    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(String message, Throwable cause) {
        super(message, cause);
    }
}
