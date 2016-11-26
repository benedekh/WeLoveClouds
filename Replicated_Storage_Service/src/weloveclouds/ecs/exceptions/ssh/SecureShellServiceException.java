package weloveclouds.ecs.exceptions.ssh;


import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-11-19.
 */
public class SecureShellServiceException extends ClientSideException {
    public SecureShellServiceException(String message) {
        super(message);
    }

    public SecureShellServiceException(String message, Exception cause) {
        super(message, cause);
    }
}
