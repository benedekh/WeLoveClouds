package weloveclouds.loadbalancer.models.cache.strategies;

/**
 * Created by Benoit on 2016-12-06.
 */
public interface IDisplacementStrategy<K> {
    K getKeyToDisplace();

    void registerGet(K key);

    void registerPut(K key);
}
