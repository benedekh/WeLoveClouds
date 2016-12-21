package weloveclouds.commons.utils;

import java.util.concurrent.locks.Lock;

public class CloseableLock implements AutoCloseable {

    private Lock lock;

    public CloseableLock(Lock lock) {
        this.lock = lock;
        this.lock.lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }



}
