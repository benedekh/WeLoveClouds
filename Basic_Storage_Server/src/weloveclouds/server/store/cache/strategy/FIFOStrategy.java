package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.store.exceptions.StorageException;

public class FIFOStrategy implements DisplacementStrategy {

    private Queue<String> fifo;

    private Logger logger;

    public FIFOStrategy() {
        this.fifo = new ArrayDeque<>();
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized String displaceKey() throws StorageException {
        try {
            String displaced = fifo.remove();
            logger.debug(CustomStringJoiner.join(" ", displaced,
                    "to be removed from cache by FIFO strategy."));
            return displaced;
        } catch (NoSuchElementException ex) {
            String errorMessage = "FIFO strategy store is empty so it cannot remove anything.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            fifo.add(key);
            logger.debug(CustomStringJoiner.join(" ", key, "is added to the FIFO strategy store."));
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for put in FIFO strategy.");
        }
    }

    @Override
    public synchronized void get(String key) {
        // FIFO strategy does not update anything
    }

    @Override
    public synchronized void remove(String key) {
        try {
            boolean isRemoved = fifo.remove(key);
            if (isRemoved) {
                logger.debug(CustomStringJoiner.join(" ", key,
                        "is removed from the FIFO strategy store."));
            }
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for remove in FIFO strategy.");
        }
    }

}
