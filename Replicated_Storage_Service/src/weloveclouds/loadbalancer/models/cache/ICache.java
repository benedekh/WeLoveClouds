package weloveclouds.loadbalancer.models.cache;

import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;

/**
 * Created by Benoit on 2016-12-06.
 */
public interface ICache<K, V> {
    V get(K key) throws UnableToFindRequestedKeyException;

    void put(K key, V value);

    void delete(K key);

    boolean isFull();
}
