package weloveclouds.loadbalancer.services.cache;

import org.apache.log4j.Logger;

import java.util.Map;

import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;
import weloveclouds.loadbalancer.models.cache.ICache;

/**
 * Created by Benoit on 2016-12-03.
 */
public class CacheService implements ICacheService<String, String> {
    private static final Logger LOGGER = Logger.getLogger(CacheService.class);
    private ICache cache;

    public CacheService(ICache cache){
        this.cache = cache;
    }

    @Override
    public String get(String key) throws UnableToFindRequestedKeyException {
        return null;
    }

    @Override
    public void put(String key, String value) {

    }
}
