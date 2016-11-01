package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import weloveclouds.server.store.exceptions.StorageException;

public class LRUStrategy implements DisplacementStrategy {

    private Deque<String> recentKeys;

    private Logger logger;

    public LRUStrategy() {
        this.recentKeys = new ArrayDeque<>();
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized String displaceKey() throws StorageException {
        try {
            // the last element of the queue is the least recently used one
            return recentKeys.removeLast();
        } catch (NoSuchElementException ex) {
            throw new StorageException("Store is empty so it cannot remove anything.");
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            recentKeys.addFirst(key);
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for put.");
        }
    }

    @Override
    public synchronized void get(String key) {
        try {
            // move the element to the head of the queue
            // because it was most recently used
            recentKeys.remove(key);
            recentKeys.addFirst(key);
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for get.");
        }
    }

    @Override
    public synchronized void remove(String key) {
        try {
            recentKeys.remove(key);
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for remove.");
        }
    }

}
