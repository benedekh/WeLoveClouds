package weloveclouds.server.services;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A common interface to those {@link IDataAccessService} implementations whose underlying storage
 * units can be moved.
 * 
 * @author Benedek
 */
public interface IMovableDataAccessService extends IDataAccessService {

    /**
     * Saves the entries from the parameter storage units into this persistent storage.
     * 
     * @param fromStorageUnits from where the entries will be copied
     */
    public void putEntries(MovableStorageUnits fromStorageUnits) throws StorageException;

    /**
     * Filters those entries from the persistent storage whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the storage units of those entries whose keys are in the given range
     * @throws StorageException if the service was not initialized yet
     */
    public MovableStorageUnits filterEntries(HashRange range) throws UninitializedServiceException;

    /**
     * Removes those entries from the persistent storage whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @throws StorageException if an error occurs
     */
    public void removeEntries(HashRange range) throws StorageException;

    /**
     * Merges those storage units which are not full yet.
     * 
     * @throws StorageException if the service was not initialized yet
     */
    public void defragment() throws UninitializedServiceException;

    /**
     * Sets the status of the data access service.
     * 
     * @throws UninitializedServiceException if the service was not initialized yet
     */
    public void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
            throws UninitializedServiceException;

    /**
     * Ring hash metadata information.
     */
    public void setRingMetadata(RingMetadata ringMetadata);

    /**
     * Sets the hash range that is managed by the data access service.
     * 
     * @param range
     */
    public void setManagedHashRange(HashRange rangeManagedByServer);

    /**
     * @return true if the data access service is initialized, false otherwise
     */
    public boolean isServiceInitialized();

}
