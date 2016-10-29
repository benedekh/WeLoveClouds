package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

public class LRUStrategy implements DisplacementStrategy {

    private Deque<String> recentKeys;

    public LRUStrategy() {
        this.recentKeys = new ArrayDeque<>();
    }

    @Override
    public String displaceKey() throws StorageException {
        try {
            // the last element of the queue is the least recently used one
            return recentKeys.removeLast();
        } catch (NoSuchElementException ex) {
            throw new StorageException("Store is empty so it cannot remove anything.");
        }
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        try {
            recentKeys.addFirst(entry.getKey());
        } catch (NullPointerException ex) {
            throw new StorageException("Entry cannot be null.");
        }
    }

    @Override
    public String getValue(String key) throws StorageException {
        // move the element to the head of the queue
        // because it was most recently used
        recentKeys.remove(key);
        recentKeys.addFirst(key);
        return "";
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            recentKeys.remove(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

}
