package weloveclouds.server.store;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

public interface IKVStore {

    public void putEntry(KVEntry entry) throws StorageException;

    public String getValue(String key) throws StorageException, ValueNotFoundException;

    public void removeEntry(String key) throws StorageException;
}
