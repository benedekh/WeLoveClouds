package weloveclouds.ecs.exceptions.distributedSystem;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.hashing.models.Hash;

/**
 * Created by Benoit on 2016-12-07.
 */
public class UnableToFindServerResponsibleForWritingException extends ServerSideException {
    public UnableToFindServerResponsibleForWritingException(Hash hash) {
        super("Unable to find server responsible for writing for: " + hash.toString());
    }

    public UnableToFindServerResponsibleForWritingException(String message) {
        super(message);
    }

    public UnableToFindServerResponsibleForWritingException(String message, Throwable cause) {
        super(message, cause);
    }
}
