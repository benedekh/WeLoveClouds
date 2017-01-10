package weloveclouds.server.services.datastore.exceptions;


import weloveclouds.server.store.exceptions.StorageException;

/**
 * The key (hash value of the key) is not managed by the data access service.
 * 
 * @author Benedek
 */
public class KeyIsNotManagedByServiceException extends StorageException {

    private static final long serialVersionUID = 6417515944143203526L;

    public KeyIsNotManagedByServiceException() {
        super("Key is not managed by Data Access Service.");
    }

}
