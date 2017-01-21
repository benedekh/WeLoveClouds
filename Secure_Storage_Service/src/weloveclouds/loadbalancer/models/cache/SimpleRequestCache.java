package weloveclouds.loadbalancer.models.cache;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Created by Benoit on 2016-12-06.
 */
public class SimpleRequestCache<K, V> implements ICache<K, V> {
    private static final Logger LOGGER = Logger.getLogger(SimpleRequestCache.class);
    private DisplacementStrategy<K> displacementStrategy;
    private ConcurrentHashMap<K, V> cache;
    private int maximumCapacity;

    @Inject
    public SimpleRequestCache(@CacheMaximalCapacity int maximumCapacity, DisplacementStrategy<K>
            displacementStrategy) {
        this.cache = new ConcurrentHashMap<>();
        this.maximumCapacity = maximumCapacity;
        this.displacementStrategy = displacementStrategy;
    }

    @Override
    public V get(K key) throws UnableToFindRequestedKeyException {
        displacementStrategy.registerGet(key);
        V value = cache.get(key);

        if (value != null) {
            return value;
        } else {
            LOGGER.debug("Unable to find key:" + key);
            throw new UnableToFindRequestedKeyException("Unable to find key:" + key);
        }
    }

    @Override
    public void put(K key, V value) {
        try {
            if (isFull() && !cache.containsKey(key)) {
                cache.remove(displacementStrategy.getKeyToDisplace());
            }
            LOGGER.debug(StringUtils.join(" ", "Caching key:", key, "with value:", value));
            cache.put(key, value);
            displacementStrategy.registerPut(key);
        } catch (StorageException e) {
            LOGGER.info(e.getMessage());
        }
    }

    @Override
    public void delete(K key) {
        LOGGER.debug(StringUtils.join(" ", "Removing key:", key));
        cache.remove(key);
        displacementStrategy.registerRemove(key);
    }

    @Override
    public boolean isFull() {
        return cache.size() >= maximumCapacity;
    }
}
