package weloveclouds.server.services.datastore.exceptions;

import weloveclouds.server.store.exceptions.StorageException;

/**
 * A write lock is active on the data access service.
 * 
 * @author Benedek
 */
public class WriteLockIsActiveException extends StorageException {

    private static final long serialVersionUID = -4956448073711231965L;

}
