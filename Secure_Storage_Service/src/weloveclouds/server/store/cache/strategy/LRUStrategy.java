package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * LRU (Least Recently Used) strategy for displacing a key from a full cache.
 * 
 * @author Benedek
 */
public class LRUStrategy implements DisplacementStrategy<String> {

    private static final Logger LOGGER = Logger.getLogger(LRUStrategy.class);

    private Deque<String> recentKeys;
    private ReentrantReadWriteLock accessLock;

    public LRUStrategy() {
        this.recentKeys = new ArrayDeque<>();
        this.accessLock = new ReentrantReadWriteLock();
    }

    @Override
    public String getStrategyName() {
        return "LRU";
    }

    @Override
    public String getKeyToDisplace() throws StorageException {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            // the last element of the queue is the least recently used one
            String displaced = recentKeys.removeLast();
            LOGGER.debug(
                    StringUtils.join(" ", displaced, "to be removed from cache by LRU strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "LRU strategy store is empty so it cannot registerRemove anything.";
            LOGGER.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public void registerPut(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            recentKeys.addFirst(key);
            LOGGER.debug(StringUtils.join(" ", key, "is added to the LRU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for registerPut in LRU strategy.");
        }
    }

    @Override
    public void registerGet(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            // move the element to the head of the queue
            // because it was most recently used
            recentKeys.remove(key);
            recentKeys.addFirst(key);
            LOGGER.debug(StringUtils.join(" ", key,
                    "is the most recently used in the LRU strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for registerGet in LRU strategy.");
        }
    }

    @Override
    public void registerRemove(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            boolean isRemoved = recentKeys.remove(key);
            if (isRemoved) {
                LOGGER.debug(StringUtils.join(" ", key, "is removed from the LRU strategy store."));
            }
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for registerRemove in LRU strategy.");
        }
    }

}
