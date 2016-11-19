package weloveclouds.server.store.cache.exceptions;

import weloveclouds.server.store.exceptions.StorageException;

public class KeyIsNotManagedByServerException extends StorageException {

    private static final long serialVersionUID = 6417515944143203526L;

    public KeyIsNotManagedByServerException(String serializedRingMetadata) {
        super(serializedRingMetadata);
    }

}
