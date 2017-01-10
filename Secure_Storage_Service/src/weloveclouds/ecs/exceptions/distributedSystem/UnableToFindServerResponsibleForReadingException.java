package weloveclouds.ecs.exceptions.distributedSystem;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.hashing.models.Hash;

/**
 * Created by Benoit on 2016-12-07.
 */
public class UnableToFindServerResponsibleForReadingException extends ServerSideException {
    public UnableToFindServerResponsibleForReadingException(Hash hash) {
        super("Unable to find server responsible for reading for: " + hash.toString());
    }

    public UnableToFindServerResponsibleForReadingException(String message) {
        super(message);
    }

    public UnableToFindServerResponsibleForReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
