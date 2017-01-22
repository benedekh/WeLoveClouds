package weloveclouds.server.store.cache.strategy;

import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents a displacement strategy that decides which key shall be removed from the cache.
 *
 * @author Benedek
 */
public interface DisplacementStrategy<K> {

    /**
     * The respective key was registerPut in the storage.
     */
    void registerPut(K key);

    /**
     * The respective key was registerGet from the storage.
     */
    void registerGet(K key);

    /**
     * The respective key was removed from the storage.
     */
    void registerRemove(K key);

    /**
     * Name of the key that shall be removed from the cache.
     *
     * @throws StorageException if an error occurs
     */
    K getKeyToDisplace() throws StorageException;

    /**
     * @return name of the strategy
     */
    String getStrategyName();
}
