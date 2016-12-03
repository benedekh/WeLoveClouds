package weloveclouds.server.services.exceptions;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * The key (hash value of the key) is not managed by the data access service.
 * 
 * @author Benedek
 */
public class KeyIsNotManagedByServiceException extends StorageException {

    private static final long serialVersionUID = 6417515944143203526L;

    /**
     * @param serializedRingMetadata the {@link RingMetadata} serialized as a String
     */
    public KeyIsNotManagedByServiceException(String serializedRingMetadata) {
        super(serializedRingMetadata);
    }

}
