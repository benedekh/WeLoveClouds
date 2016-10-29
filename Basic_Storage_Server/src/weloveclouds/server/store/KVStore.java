package weloveclouds.server.store;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.persistent.KVPersistentStorage;

public class KVStore implements IKVStore {

    private KVCache cache;
    private KVPersistentStorage persistentStorage;

    public KVStore(KVCache cache, KVPersistentStorage persistentStorage) {
        this.cache = cache;
        this.persistentStorage = persistentStorage;
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.putEntry(entry);
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
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
    public void removeEntry(String key) throws StorageException {
        // implicit notification will go the cache as well
        // throw the persistent store
        persistentStorage.removeEntry(key);
    }

}
