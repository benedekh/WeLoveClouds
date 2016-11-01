package weloveclouds.server.store.cache.strategy;

import weloveclouds.server.store.IKVStoreNotification;
import weloveclouds.server.store.exceptions.StorageException;

public interface DisplacementStrategy extends IKVStoreNotification {

    public String displaceKey() throws StorageException;
}
