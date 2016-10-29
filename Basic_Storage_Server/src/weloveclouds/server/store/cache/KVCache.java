package weloveclouds.server.store.cache;

import java.util.Map;
import java.util.TreeMap;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.IKVStore;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.exceptions.StorageException;

public class KVCache implements IKVStore {

    private int currentSize;
    private int maxSize;

    private Map<String, String> cache;
    private DisplacementStrategy strategy;

    public KVCache(DisplacementStrategy strategy, int maxSize) {
        this.strategy = strategy;
        this.maxSize = maxSize;

        this.cache = new TreeMap<>();
        this.currentSize = 0;
    }

    @Override
    public synchronized void putEntry(KVEntry entry) throws StorageException {
        try {
            String key = entry.getKey();
            String value = entry.getValue();

            if (currentSize == maxSize) {
                strategy.displaceEntryFromCache(this);
                currentSize--;
            }
            cache.put(key, value);
            currentSize++;
        } catch (NullPointerException ex) {
            throw new StorageException("Key or value is null.");
        } catch (IllegalArgumentException ex) {
            throw new StorageException(
                    "Some property of the key or value prevents it from being stored in the cache.");
        }
    }

    @Override
    public synchronized String getValue(String key) throws StorageException {
        try {
            return cache.get(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        try {
            cache.remove(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

}
