package weloveclouds.server.store.cache.strategy;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.IKVStore;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Extends IKVStore because it may need to have the same information as the cache has. (To be
 * up-to-date with the latest adds and removes in the cache.)
 */
public interface DisplacementStrategy extends IKVStore {

    public KVEntry displaceEntry() throws StorageException;
}
