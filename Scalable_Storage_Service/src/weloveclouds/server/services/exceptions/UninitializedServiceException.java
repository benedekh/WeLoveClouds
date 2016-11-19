package weloveclouds.server.services.exceptions;

import weloveclouds.server.store.exceptions.StorageException;

public class UninitializedServiceException extends StorageException {

    private static final long serialVersionUID = 5011561569278710497L;

    public UninitializedServiceException() {
        super("Data access service is not initialized yet.");
    }

}
