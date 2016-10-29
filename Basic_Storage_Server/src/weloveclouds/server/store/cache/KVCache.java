package weloveclouds.server.store.cache;

import java.util.Map;
import java.util.TreeMap;

import weloveclouds.server.store.cache.exceptions.CacheException;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

public class KVCache {

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

    public synchronized void putEntry(String key, String value) throws CacheException {
        try {
            if (currentSize == maxSize) {
                strategy.displaceEntryFromCache(this);
                currentSize--;
            }
            cache.put(key, value);
            currentSize++;
        } catch (NullPointerException ex) {
            throw new CacheException("Key or value is null.");
        } catch (IllegalArgumentException ex) {
            throw new CacheException(
                    "Some property of the key or value prevents it from being stored in the cache.");
        }
    }

    public synchronized String getValue(String key) throws CacheException {
        try {
            return cache.get(key);
        } catch (NullPointerException ex) {
            throw new CacheException("Key cannot be null.");
        }
    }

    public synchronized void removeEntry(String key) throws CacheException {
        try {
            cache.remove(key);
        } catch (NullPointerException ex) {
            throw new CacheException("Key cannot be null.");
        }
    }

}
