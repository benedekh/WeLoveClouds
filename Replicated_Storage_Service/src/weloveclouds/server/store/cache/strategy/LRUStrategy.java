package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * LRU (Least Recently Used) strategy for displacing a key from a full cache.
 * 
 * @author Benedek
 */
public class LRUStrategy implements DisplacementStrategy {

    private static final Logger LOGGER = Logger.getLogger(LRUStrategy.class);

    private Deque<String> recentKeys;

    public LRUStrategy() {
        this.recentKeys = new ArrayDeque<>();
    }

    @Override
    public String getStrategyName() {
        return "LRU";
    }

    @Override
    public synchronized String displaceKey() throws StorageException {
        try {
            // the last element of the queue is the least recently used one
            String displaced = recentKeys.removeLast();
            LOGGER.debug(CustomStringJoiner.join(" ", displaced,
                    "to be removed from cache by LRU strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "LRU strategy store is empty so it cannot remove anything.";
            LOGGER.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            recentKeys.addFirst(key);
            LOGGER.debug(CustomStringJoiner.join(" ", key, "is added to the LRU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for put in LRU strategy.");
        }
    }

    @Override
    public synchronized void get(String key) {
        try {
            // move the element to the head of the queue
            // because it was most recently used
            recentKeys.remove(key);
            recentKeys.addFirst(key);
            LOGGER.debug(CustomStringJoiner.join(" ", key,
                    "is the most recently used in the LRU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for get in LRU strategy.");
        }
    }

    @Override
    public synchronized void remove(String key) {
        try {
            boolean isRemoved = recentKeys.remove(key);
            if (isRemoved) {
                LOGGER.debug(CustomStringJoiner.join(" ", key,
                        "is removed from the LRU strategy store."));
            }
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for remove in LRU strategy.");
        }
    }

}
