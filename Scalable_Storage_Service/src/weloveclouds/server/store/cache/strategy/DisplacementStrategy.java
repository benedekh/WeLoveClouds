package weloveclouds.server.store.cache.strategy;

import weloveclouds.server.store.IKVStoreNotification;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents a displacement startegy that decides which key shall be removed from the cache-
 * 
 * @author Benedek
 */
public interface DisplacementStrategy extends IKVStoreNotification {

    /**
     * Name of the key that shall be removed from the cache.
     * 
     * @throws StorageException if an error occurs
     */
    public String displaceKey() throws StorageException;
}
