package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * FIFO (First In First Out) strategy for displacing a key from a full cache.
 *  
 * @author Benedek
 */
public class FIFOStrategy implements DisplacementStrategy {

    private static final Logger LOGGER = Logger.getLogger(FIFOStrategy.class);
    
    private Queue<String> fifo;

    public FIFOStrategy() {
        this.fifo = new ArrayDeque<>();
    }

    @Override
    public synchronized String displaceKey() throws StorageException {
        try {
            String displaced = fifo.remove();
            LOGGER.debug(CustomStringJoiner.join(" ", displaced,
                    "to be removed from cache by FIFO strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "FIFO strategy store is empty so it cannot remove anything.";
            LOGGER.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            fifo.add(key);
            LOGGER.debug(CustomStringJoiner.join(" ", key, "is added to the FIFO strategy store."));
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for put in FIFO strategy.");
        }
    }

    @Override
    public synchronized void get(String key) {
        // FIFO strategy does not update anything
    }

    public synchronized void remove(String key) {
        try {
            boolean isRemoved = fifo.remove(key);
            if (isRemoved) {
                LOGGER.debug(CustomStringJoiner.join(" ", key,
                        "is removed from the FIFO strategy store."));
            }
        } catch (NullPointerException ex) {
            LOGGER.error("Key cannot be null for remove in FIFO strategy.");
        }
    }
}
