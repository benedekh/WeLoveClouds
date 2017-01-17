package weloveclouds.server.services.datastore;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.PutType;
import weloveclouds.server.store.storage.KVPersistentStorage;

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
        LOGGER.debug("DataAccessService initialization started.");
        this.cache = cache;
        this.persistentStorage = persistentStorage;
        this.persistentStorage.addObserver(cache);
        LOGGER.debug("DataAccessService initialization finished.");
    }

    @Override
    public PutType putEntry(KVEntry entry) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        PutType response = persistentStorage.putEntry(entry);
        LOGGER.debug(StringUtils.join(" ", entry, "is stored."));
        return response;
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        String value = null;
        try {
            value = cache.getValue(key);
            LOGGER.debug(StringUtils.join(" ", "Value", value, "for key", key,
                    "is retireved from cache."));
        } catch (ValueNotFoundException ex) {
            value = persistentStorage.getValue(key);
            LOGGER.debug(StringUtils.join(" ", "Value", value, "for key", key,
                    "is retireved from persistent storage."));
            cache.putEntry(new KVEntry(key, value));
        }
        return value;
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.removeEntry(key);
        LOGGER.debug(StringUtils.join(" ", key, "is removed."));
    }

}
