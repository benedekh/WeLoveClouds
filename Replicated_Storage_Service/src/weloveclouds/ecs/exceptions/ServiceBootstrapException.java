package weloveclouds.ecs.exceptions;


import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-11-21.
 */
public class ServiceBootstrapException extends ClientSideException {
    public ServiceBootstrapException(String message) {
        super(message);
    }

    public ServiceBootstrapException(String message, Exception cause) {
        super(message, cause);
    }
}
