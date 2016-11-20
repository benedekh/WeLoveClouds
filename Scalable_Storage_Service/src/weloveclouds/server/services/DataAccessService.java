package weloveclouds.server.services;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

/**
 * Represents an abstract access layer to the key-value store. It include the {@link KVCache} and
 * the {@link KVPersistentStorage} as well.
 * 
 * @author Benedek
 */
public class DataAccessService implements IDataAccessService {

    private static final Logger LOGGER = Logger.getLogger(DataAccessService.class);

    private KVCache cache;
    private KVPersistentStorage persistentStorage;

    public DataAccessService(KVCache cache, KVPersistentStorage persistentStorage) {
        this.cache = cache;
        this.persistentStorage = persistentStorage;
        this.persistentStorage.addObserver(cache);
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        PutType response = persistentStorage.putEntry(entry);
        LOGGER.debug(CustomStringJoiner.join(" ", entry.toString(), "is stored."));
        return response;
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        String value = null;
        try {
            value = cache.getValue(key);
            LOGGER.debug(CustomStringJoiner.join(" ", "Value", value, "for key", key,
                    "is retireved from cache."));
        } catch (ValueNotFoundException ex) {
            value = persistentStorage.getValue(key);
            LOGGER.debug(CustomStringJoiner.join(" ", "Value", value, "for key", key,
                    "is retireved from persistent storage."));
            cache.putEntry(new KVEntry(key, value));
        }
        return value;
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.removeEntry(key);
        LOGGER.debug(CustomStringJoiner.join(" ", key, "is removed."));
    }

}
