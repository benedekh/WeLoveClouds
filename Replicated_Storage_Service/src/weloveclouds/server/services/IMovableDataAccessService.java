package weloveclouds.server.services;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;
import weloveclouds.server.store.PutType;
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
     * Puts an entry into the persistent storage without checking either the role (@link
     * weloveclouds.server.models.replication.Role) of this server regarding that key, or the fact
     * that the key is managed by the server.
     * 
     * @param entry that has to be put in the persistent storage
     * @return {@link PutType#INSERT}} if key was stored for the first time in the storage, or a
     *         {@link PutType#UPDATE} if the key was already stored
     * @throws StorageException if an error occurs
     */
    public PutType putEntryWithoutAuthorization(KVEntry entry) throws StorageException;

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
     * Removes an entry denoted by its key from the persistent storage without checking either the
     * role (@link weloveclouds.server.models.replication.Role) of this server regarding that key,
     * or the fact that the key is managed by the server.
     * 
     * @param key the key of the entry that shall be removed
     * @throws StorageException if an error occurs
     */
    public void removeEntryWithoutAuthorization(String key) throws StorageException;

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
     * Sets the hash ranges that are managed by the data access service.
     */
    public void setManagedHashRanges(HashRangesWithRoles rangesManagedByServer);

    /**
     * @return true if the data access service is initialized, false otherwise
     */
    public boolean isServiceInitialized();

}
