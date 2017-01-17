package weloveclouds.server.store.exceptions;

import weloveclouds.server.services.datastore.DataAccessService;

/**
 * Represents an exception that occurred in the {@link DataAccessService}.
 * 
 * @author Benedek
 */
public class StorageException extends Exception {

    private static final long serialVersionUID = 3037481656022380929L;

    public StorageException() {

    }

    public StorageException(String message) {
        super(message);
    }

}
