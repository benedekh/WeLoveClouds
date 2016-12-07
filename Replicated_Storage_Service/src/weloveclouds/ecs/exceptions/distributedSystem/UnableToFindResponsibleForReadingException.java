package weloveclouds.ecs.exceptions.distributedSystem;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.hashing.models.Hash;

/**
 * Created by Benoit on 2016-12-07.
 */
public class UnableToFindResponsibleForReadingException extends ServerSideException {
    public UnableToFindResponsibleForReadingException(Hash hash) {
        super("Unable to find responsible for reading with hash key: " + hash.toString());
    }

    public UnableToFindResponsibleForReadingException(String message) {
        super(message);
    }

    public UnableToFindResponsibleForReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
