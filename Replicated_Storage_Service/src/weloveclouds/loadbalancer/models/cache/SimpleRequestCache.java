package weloveclouds.loadbalancer.models.cache;

import com.google.inject.Inject;

import java.util.LinkedHashMap;
import java.util.Map;

import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;
import weloveclouds.loadbalancer.models.cache.strategies.IDisplacementStrategy;

/**
 * Created by Benoit on 2016-12-06.
 */
public class SimpleRequestCache<K, V> implements ICache<K, V> {
    private IDisplacementStrategy<K> displacementStrategy;
    private Map<K, V> cache;
    private int maximumCapacity;

    @Inject
    public SimpleRequestCache(@CacheMaximalCapacity int maximumCapacity, IDisplacementStrategy<K>
            displacementStrategy) {
        this.cache = new LinkedHashMap<>();
        this.maximumCapacity = maximumCapacity;
    }

    @Override
    public V get(K key) throws UnableToFindRequestedKeyException {
        displacementStrategy.registerGet(key);
        return null;
    }

    @Override
    public void put(K key, V value) {
        if (cache.size() >= maximumCapacity && !cache.containsKey(key)) {
            cache.remove(displacementStrategy.getKeyToDisplace());
        }
        cache.put(key, value);
        displacementStrategy.registerPut(key);
    }

    @Override
    public void delete(K key) {
        cache.remove(key);
    }
}
