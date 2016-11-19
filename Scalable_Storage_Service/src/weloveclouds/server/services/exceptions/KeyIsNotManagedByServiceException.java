package weloveclouds.server.services.exceptions;

import weloveclouds.server.store.exceptions.StorageException;

public class KeyIsNotManagedByServiceException extends StorageException {

    private static final long serialVersionUID = 6417515944143203526L;

    public KeyIsNotManagedByServiceException(String serializedRingMetadata) {
        super(serializedRingMetadata);
    }

}
