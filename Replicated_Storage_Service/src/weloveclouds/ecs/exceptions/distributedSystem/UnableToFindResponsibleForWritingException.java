package weloveclouds.ecs.exceptions.distributedSystem;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.hashing.models.Hash;

/**
 * Created by Benoit on 2016-12-07.
 */
public class UnableToFindResponsibleForWritingException extends ServerSideException {
    public UnableToFindResponsibleForWritingException(Hash hash) {
        super("Unable to find responsible for writing with hash key: " + hash.toString());
    }

    public UnableToFindResponsibleForWritingException(String message) {
        super(message);
    }

    public UnableToFindResponsibleForWritingException(String message, Throwable cause) {
        super(message, cause);
    }
}
