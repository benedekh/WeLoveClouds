package weloveclouds.server.store.cache.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.models.KeyFrequency;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * LFU (Least Frequently Used) strategy for displacing a key from a full cache.
 * 
 * @author Benedek
 */
public class LFUStrategy implements DisplacementStrategy {

    private static final Logger LOGGER = Logger.getLogger(LFUStrategy.class);

    private Map<String, KeyFrequency> keyFrequencyPairs;

    public LFUStrategy() {
        this.keyFrequencyPairs = new HashMap<>();
    }

    @Override
    public synchronized String displaceKey() throws StorageException {
        try {
            // order by frequency, the least frequent will be the first
            SortedSet<KeyFrequency> sorted = new TreeSet<>(keyFrequencyPairs.values());
            KeyFrequency first = sorted.first();
            String displaced = first.getKey();
            remove(displaced);
            LOGGER.debug(CustomStringJoiner.join(" ", displaced,
                    "to be removed from cache by LFU strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "LFU strategy store is empty so it cannot remove anything.";
            LOGGER.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            KeyFrequency keyFrequency = new KeyFrequency(key, 0);
            keyFrequencyPairs.put(key, keyFrequency);
            LOGGER.debug(CustomStringJoiner.join(" ", key, "is added to the LFU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for put in LFU strategy.");
        }
    }

    @Override
    public synchronized void get(String key) {
        try {
            KeyFrequency keyFrequency = keyFrequencyPairs.get(key);
            keyFrequency.increaseFrequencyByOne();
            LOGGER.debug(CustomStringJoiner.join(" ", keyFrequency.toString(),
                    "is updated in the LFU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for get in LFU strategy.");
        }
    }

    @Override
    public synchronized void remove(String key) {
        try {
            KeyFrequency removed = keyFrequencyPairs.remove(key);
            if (removed != null) {
                LOGGER.debug(CustomStringJoiner.join(" ", key,
                        "is removed from the LFU strategy store."));
            }
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for remove in LFU strategy.");
        }
    }

}
