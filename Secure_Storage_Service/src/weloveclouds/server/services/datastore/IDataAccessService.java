package weloveclouds.server.services.datastore;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.PutType;

/**
 * A common interface for the data access layer through which key-value pairs can be stored on the
 * server.
 * 
 * @author Benedek
 */
public interface IDataAccessService {

    /**
     * Insert or update a key-value pair in the storage.
     * 
     * @param entry that contains the key and the value
     * @return {@link PutType#INSERT}} if key was stored for the first time in the storage, or a
     *         {@link PutType#UPDATE} if the key was already stored
     * @throws StorageException if any error occurs
     */
    PutType putEntry(KVEntry entry) throws StorageException;

    /**
     * Gets the respective value for the referred key from the storage.
     * 
     * @throws StorageException if any error occurs
     * @throws ValueNotFoundException if the key was not stored in the storage yet
     */
    String getValue(String key) throws StorageException, ValueNotFoundException;

    /**
     * Removes the respective key along with the stored value from the storage
     * 
     * @throws StorageException if any error occurs
     */
    void removeEntry(String key) throws StorageException;

}
