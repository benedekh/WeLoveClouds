package weloveclouds.server.store.cache;

import java.util.Map;
import java.util.TreeMap;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.IKVStore;
import weloveclouds.server.store.exceptions.StorageException;

public class Cache implements IKVStore {

    private int currentSize;
    private int capacity;

    private Map<String, String> cache;

    public Cache(int maxSize) {
        this.capacity = maxSize;

        this.cache = new TreeMap<>();
        this.currentSize = 0;
    }

    public boolean isFull() {
        return currentSize == capacity;
    }

    public Map<String, String> getCache() {
        return cache;
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        try {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                throw new NullPointerException();
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
    public String getValue(String key) throws StorageException {
        try {
            return cache.get(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            cache.remove(key);
            currentSize--;
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

}
