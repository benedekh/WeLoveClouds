package weloveclouds.server.store.cache.strategy;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

public class LRUStrategy implements DisplacementStrategy {

    @Override
    public KVEntry displaceEntry() throws StorageException {
        // TODO Auto-generated method stub
        return null;
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
