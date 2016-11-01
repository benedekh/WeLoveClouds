package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.log4j.Logger;

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
            return fifo.remove();
        } catch (NoSuchElementException ex) {
            throw new StorageException("Store is empty so it cannot remove anything.");
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            fifo.add(key);
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for put.");
        }
    }

    @Override
    public synchronized void get(String key) {
        // FIFO strategy does not update anything
    }

    @Override
    public synchronized void remove(String key) {
        try {
            fifo.remove(key);
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for remove.");
        }
    }

}
