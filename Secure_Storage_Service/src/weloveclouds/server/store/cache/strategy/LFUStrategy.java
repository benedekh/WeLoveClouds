package weloveclouds.server.store.cache.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KeyFrequency;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * LFU (Least Frequently Used) strategy for displacing a key from a full cache.
 * 
 * @author Benedek
 */
public class LFUStrategy implements DisplacementStrategy {

    private static final Logger LOGGER = Logger.getLogger(LFUStrategy.class);

    private Map<String, KeyFrequency> keyFrequencyPairs;
    private ReentrantReadWriteLock accessLock;

    public LFUStrategy() {
        this.keyFrequencyPairs = new HashMap<>();
        this.accessLock = new ReentrantReadWriteLock();
    }

    @Override
    public String getStrategyName() {
        return "LFU";
    }

    @Override
    public String displaceKey() throws StorageException {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            // order by frequency, the least frequent will be the first
            SortedSet<KeyFrequency> sorted = new TreeSet<>(keyFrequencyPairs.values());
            KeyFrequency first = sorted.first();
            String displaced = first.getKey();
            remove(displaced);
            LOGGER.debug(
                    StringUtils.join(" ", displaced, "to be removed from cache by LFU strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "LFU strategy store is empty so it cannot remove anything.";
            LOGGER.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public void put(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            KeyFrequency keyFrequency = new KeyFrequency(key, 0);
            keyFrequencyPairs.put(key, keyFrequency);
            LOGGER.debug(StringUtils.join(" ", key, "is added to the LFU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for put in LFU strategy.");
        }
    }

    @Override
    public void get(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            KeyFrequency keyFrequency = keyFrequencyPairs.get(key);
            keyFrequency.increaseFrequencyByOne();
            LOGGER.debug(
                    StringUtils.join(" ", keyFrequency, "is updated in the LFU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for get in LFU strategy.");
        }
    }

    @Override
    public void remove(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            KeyFrequency removed = keyFrequencyPairs.remove(key);
            if (removed != null) {
                LOGGER.debug(StringUtils.join(" ", key, "is removed from the LFU strategy store."));
            }
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for remove in LFU strategy.");
        }
    }

}
