package weloveclouds.server.store.cache;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.IEntryChangeNotifyable;
import weloveclouds.server.store.IKVStore;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

public class KVCache implements IKVStore, IEntryChangeNotifyable {

    private Map<String, String> cache;
    private int currentSize;
    private int capacity;

    private DisplacementStrategy displacementStrategy;
    private Logger logger;

    public KVCache(int maxSize, DisplacementStrategy displacementStrategy) {
        this.capacity = maxSize;
        this.displacementStrategy = displacementStrategy;

        this.cache = new TreeMap<>();
        this.currentSize = 0;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        try {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                throw new StorageException("Value cannot be null.");
            }

            if (currentSize == capacity) {
                KVEntry displaced = displacementStrategy.displaceEntry();
                removeEntry(displaced.getKey());
            }

            cache.put(key, value);
            currentSize++;
            displacementStrategy.putEntry(entry);
        } catch (NullPointerException ex) {
            throw new StorageException("Key or value is null.");
        } catch (IllegalArgumentException ex) {
            throw new StorageException(
                    "Some property of the key or value prevents it from being stored in the cache.");
        }
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        try {
            String value = cache.get(key);
            if (value == null) {
                throw new ValueNotFoundException(key);
            } else {
                displacementStrategy.getValue(key);
                return value;
            }
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            cache.remove(key);
            currentSize--;
            displacementStrategy.removeEntry(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

    @Override
    public void put(KVEntry entry) {
        try {
            putEntry(entry);
        } catch (StorageException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void remove(String key) {
        try {
            removeEntry(key);
        } catch (StorageException e) {
            logger.error(e.getMessage());
        }
    }

}
