package weloveclouds.server.services.datastore;

import java.util.Set;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.PutType;

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
    public void putEntries(Set<MovableStorageUnit> fromStorageUnits) throws StorageException;

    /**
     * Puts an entry into the persistent storage without checking the role (@link Role) of this
     * server regarding that key.
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
    public Set<MovableStorageUnit> filterEntries(HashRange range)
            throws UninitializedServiceException;

    /**
     * Removes those entries from the persistent storage whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @throws StorageException if an error occurs
     */
    public void removeEntries(HashRange range) throws StorageException;

    /**
     * Removes an entry denoted by its key from the persistent storage without checking the role
     * (@link Role) of this server regarding that key.
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
     * @return the recent service status
     */
    public DataAccessServiceStatus getServiceStatus();

    /**
     * Sets the ring hash metadata information.
     */
    public void setRingMetadata(RingMetadata ringMetadata);

    /**
     * @return ring hash metadata information
     */
    public RingMetadata getRingMetadata();

    /**
     * Sets the hash ranges that are managed by the data access service.
     * 
     * @param readRanges {@link HashRange} ranges for which the server has READ privilege
     * @param writeRange {@link HashRange} range for which the server has WRITE privilege
     */
    public void setManagedHashRanges(Set<HashRange> readRanges, HashRange writeRange);

    /**
     * @return true if the data access service is initialized, false otherwise
     */
    public boolean isServiceInitialized();

    /**
     * @return the simulated version of the data access service
     */
    public SimulatedMovableDataAccessService getSimulatedDataAccessService();

}
