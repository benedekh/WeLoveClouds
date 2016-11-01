package weloveclouds.server.store;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.persistent.KVPersistentStorage;

public class KVStore implements IKVStore {

    private KVCache cache;
    private KVPersistentStorage persistentStorage;

    private Logger logger;

    public KVStore(KVCache cache, KVPersistentStorage persistentStorage) {
        this.cache = cache;
        this.persistentStorage = persistentStorage;
        this.persistentStorage.addObserver(cache);

        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized void putEntry(KVEntry entry) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.putEntry(entry);
        logger.debug(CustomStringJoiner.join(" ", entry.toString(), "is stored."));
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        String value = null;
        try {
            value = cache.getValue(key);
            logger.debug(CustomStringJoiner.join(" ", "Value", value, "for key", key,
                    "is retireved from cache stored."));
        } catch (ValueNotFoundException ex) {
            value = persistentStorage.getValue(key);
            logger.debug(CustomStringJoiner.join(" ", "Value", value, "for key", key,
                    "is retireved from persistent storage."));
            putEntry(new KVEntry(key, value));
        }
        return value;
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.removeEntry(key);
        logger.debug(CustomStringJoiner.join(" ", key, "is removed."));
    }

}
