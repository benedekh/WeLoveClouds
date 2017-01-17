package weloveclouds.commons.utils;

import java.util.concurrent.locks.Lock;

/**
 * Represents a lock which can be closed (released) as soon as the try-with-resources block is left.
 * 
 * @author Benedek
 */
public class CloseableLock implements AutoCloseable {

    private Lock lock;

    /**
     * Acquires the lock.
     */
    public CloseableLock(Lock lock) {
        this.lock = lock;
        this.lock.lock();
    }

    /**
     * Releases the lock.
     */
    @Override
    public void close() {
        lock.unlock();
    }



}
