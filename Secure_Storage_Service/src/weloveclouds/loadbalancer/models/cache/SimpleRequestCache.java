package weloveclouds.loadbalancer.models.cache;

import com.google.inject.Inject;

import java.util.concurrent.ConcurrentHashMap;

import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;
import weloveclouds.loadbalancer.models.cache.strategies.IDisplacementStrategy;

/**
 * Created by Benoit on 2016-12-06.
 */
public class SimpleRequestCache<K, V> implements ICache<K, V> {
    private IDisplacementStrategy<K> displacementStrategy;
    private ConcurrentHashMap<K, V> cache;
    private int maximumCapacity;

    @Inject
    public SimpleRequestCache(@CacheMaximalCapacity int maximumCapacity, IDisplacementStrategy<K>
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
            throw new UnableToFindRequestedKeyException("Unable to find key:" + key);
        }
    }

    @Override
    public void put(K key, V value) {
        if (isFull() && !cache.containsKey(key)) {
            cache.remove(displacementStrategy.getKeyToDisplace());
        }
        cache.put(key, value);
        displacementStrategy.registerPut(key);
    }

    @Override
    public void delete(K key) {
        cache.remove(key);
    }

    @Override
    public boolean isFull() {
        return cache.size() >= maximumCapacity;
    }
}
