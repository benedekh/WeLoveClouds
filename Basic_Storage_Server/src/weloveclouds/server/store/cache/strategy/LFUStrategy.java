package weloveclouds.server.store.cache.strategy;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.IKVStore;
import weloveclouds.server.store.cache.Cache;
import weloveclouds.server.store.exceptions.StorageException;

public class LFUStrategy implements DisplacementStrategy {

    @Override
    public KVEntry displaceEntryFromStore(IKVStore cache) throws StorageException {
        // TODO Auto-generated method stub
        KVEntry toBeRemoved = null;
        
        cache.removeEntry(toBeRemoved.getKey());
        removeEntry(toBeRemoved.getKey());
        return toBeRemoved;
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getValue(String key) throws StorageException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        // TODO Auto-generated method stub
        
    }



}
