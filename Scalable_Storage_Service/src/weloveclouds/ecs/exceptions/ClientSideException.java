package weloveclouds.ecs.exceptions;

/**
 * Created by Benoit on 2016-11-17.
 */
public class ClientSideException extends Exception {
    public ClientSideException(String message) {
        super(message);
    }

    public ClientSideException(String message, Exception cause) {
        super(message, cause);
    }
}
