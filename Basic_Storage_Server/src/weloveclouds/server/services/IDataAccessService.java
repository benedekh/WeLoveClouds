package weloveclouds.server.services;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

public interface IDataAccessService {

    void putEntry(KVEntry entry) throws StorageException;

    String getValue(String key) throws StorageException, ValueNotFoundException;

    void removeEntry(String key) throws StorageException;
}
