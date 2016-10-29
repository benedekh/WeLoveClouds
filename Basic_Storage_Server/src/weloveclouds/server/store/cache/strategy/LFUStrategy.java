package weloveclouds.server.store.cache.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.kvstore.KeyFrequency;
import weloveclouds.server.store.exceptions.StorageException;

public class LFUStrategy implements DisplacementStrategy {

    private Map<String, KeyFrequency> keyFrequencyPairs;

    public LFUStrategy() {
        this.keyFrequencyPairs = new HashMap<>();
    }

    @Override
    public String displaceKey() throws StorageException {
        try {
            // order by frequency, the least frequent will be the first
            SortedSet<KeyFrequency> sorted = new TreeSet<>(keyFrequencyPairs.values());
            KeyFrequency first = sorted.first();
            return first.getKey();
        } catch (NoSuchElementException ex) {
            throw new StorageException("Store is empty so it cannot remove anything.");
        }
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        try {
            String key = entry.getKey();
            KeyFrequency keyFrequency = new KeyFrequency(key, 0);
            keyFrequencyPairs.put(key, keyFrequency);;
        } catch (NullPointerException ex) {
            throw new StorageException("Entry cannot be null.");
        }
    }

    @Override
    public String getValue(String key) throws StorageException {
        try {
            KeyFrequency keyFrequency = keyFrequencyPairs.get(key);
            keyFrequency.increaseFrequency();
            return "";
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            keyFrequencyPairs.remove(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }



}
