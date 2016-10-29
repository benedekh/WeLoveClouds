package weloveclouds.server.store;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.cache.Cache;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.persistent.KVPersistentStorage;

public class KVStore implements IKVStore {

    private Cache cache;
    private KVPersistentStorage persistent;
    private DisplacementStrategy strategy;

    public KVStore(Cache cache, KVPersistentStorage persistent, DisplacementStrategy strategy) {
        this.cache = cache;
        this.persistent = persistent;
        this.strategy = strategy;
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        if (cache.isFull()) {
            KVEntry displaced = strategy.displaceEntryFromStore(cache);
            persistent.putEntry(displaced);
        }
        cache.putEntry(entry);
        strategy.putEntry(entry);
    }

    @Override
    public String getValue(String key) throws StorageException {
        String value = cache.getValue(key);
        if (value == null) {
            // if is not in the cache, then get it from the persistent storage and put in the caches
            value = persistent.getValue(key);
            putEntry(new KVEntry(key, value));
            // remove from persistent because it is stored in the cache
            persistent.removeEntry(key);
        } else {
            // if it was in the cache then update the strategy information
            strategy.getValue(key);
        }
        return value;
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            cache.removeEntry(key);
            strategy.removeEntry(key);
        } catch (StorageException ex) {
            persistent.removeEntry(key);
        }
    }

}
