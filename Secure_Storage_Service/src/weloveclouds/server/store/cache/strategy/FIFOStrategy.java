package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * FIFO (First In First Out) strategy for displacing a key from a full cache.
 *
 * @author Benedek
 */
public class FIFOStrategy implements DisplacementStrategy<String> {

    private static final Logger LOGGER = Logger.getLogger(FIFOStrategy.class);

    private Queue<String> fifo;
    private ReentrantReadWriteLock accessLock;

    public FIFOStrategy() {
        this.fifo = new ArrayDeque<>();
        this.accessLock = new ReentrantReadWriteLock();
    }

    @Override
    public String getStrategyName() {
        return "FIFO";
    }

    @Override
    public String getKeyToDisplace() throws StorageException {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            String displaced = fifo.remove();
            LOGGER.debug(
                    StringUtils.join(" ", displaced, "to be removed from cache by FIFO strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "FIFO strategy store is empty so it cannot registerRemove anything.";
            LOGGER.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public void registerPut(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            fifo.add(key);
            LOGGER.debug(StringUtils.join(" ", key, "is added to the FIFO strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for registerPut in FIFO strategy.");
        }
    }

    @Override
    public void registerGet(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.readLock())) {
            // FIFO strategy does not update anything
        }
    }

    public void registerRemove(String key) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            boolean isRemoved = fifo.remove(key);
            if (isRemoved) {
                LOGGER.debug(
                        StringUtils.join(" ", key, "is removed from the FIFO strategy store."));
            }
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for registerRemove in FIFO strategy.");
        }
    }
}
