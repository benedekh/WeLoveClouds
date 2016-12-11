package weloveclouds.loadbalancer.exceptions.cache;

import weloveclouds.commons.exceptions.ServerSideException;

/**
 * Created by Benoit on 2016-12-06.
 */
public class UnableToFindRequestedKeyException extends ServerSideException {
    public UnableToFindRequestedKeyException(String message) {
        super(message);
    }

    public UnableToFindRequestedKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
