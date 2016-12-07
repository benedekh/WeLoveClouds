package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;
import weloveclouds.loadbalancer.models.cache.ICache;

/**
 * Created by Benoit on 2016-12-03.
 */
public class CacheService implements ICacheService<String, String> {
    private static final Logger LOGGER = Logger.getLogger(CacheService.class);
    private ICache<String, String> cache;

    @Inject
    public CacheService(ICache<String,String> cache) {
        this.cache = cache;
    }

    @Override
    synchronized public String get(String key) throws UnableToFindRequestedKeyException {
        return cache.get(key);
    }

    @Override
    synchronized public void put(String key, String value) {
        cache.put(key, value);
    }
}
