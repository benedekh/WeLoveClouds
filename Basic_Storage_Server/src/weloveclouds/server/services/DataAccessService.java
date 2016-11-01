package weloveclouds.server.services;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.persistent.KVPersistentStorage;

public class DataAccessService implements IDataAccessService {

    private KVCache cache;
    private KVPersistentStorage persistentStorage;

    public DataAccessService(KVCache cache, KVPersistentStorage persistentStorage) {
        this.cache = cache;
        this.persistentStorage = persistentStorage;
        this.persistentStorage.addObserver(cache);
    }

    @Override
    public synchronized void putEntry(KVEntry entry) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.putEntry(entry);
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        String value = null;
        try {
            value = cache.getValue(key);
        } catch (ValueNotFoundException ex) {
            value = persistentStorage.getValue(key);
            putEntry(new KVEntry(key, value));
        }
        return value;
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.removeEntry(key);
    }

}
