package weloveclouds.loadbalancer.services;

import weloveclouds.loadbalancer.exceptions.cache.UnableToFindRequestedKeyException;

/**
 * Created by Benoit on 2016-12-03.
 */
public interface ICacheService<K, V> {
    V get(String key) throws UnableToFindRequestedKeyException;

    void put(K key, V value);

    void delete(K key);
}
