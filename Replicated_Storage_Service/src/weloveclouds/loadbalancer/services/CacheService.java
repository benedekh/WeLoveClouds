package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;
import weloveclouds.loadbalancer.models.cache.ICache;

/**
 * Created by Benoit on 2016-12-03.
 */
@Singleton
public class CacheService implements ICacheService<String, String> {
    private static final Logger LOGGER = Logger.getLogger(CacheService.class);
    private ICache<String, String> cache;
    private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    @Inject
    public CacheService(ICache<String, String> cache) {
        this.cache = cache;
    }

    @Override
    public String get(String key) throws UnableToFindRequestedKeyException {
        String value;
        try {
            reentrantReadWriteLock.readLock().lock();
            value = cache.get(key);
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
        return value;
    }

    @Override
    public void put(String key, String value) {
        try {
            reentrantReadWriteLock.writeLock().lock();
            cache.put(key, value);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(String key) {
        try {
            reentrantReadWriteLock.writeLock().lock();
            cache.delete(key);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }
}
