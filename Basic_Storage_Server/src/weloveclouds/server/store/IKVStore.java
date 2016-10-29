package weloveclouds.server.store;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

public interface IKVStore {

    public void putEntry(KVEntry entry) throws StorageException;

    public String getValue(String key) throws StorageException;

    public void removeEntry(String key) throws StorageException;
}
