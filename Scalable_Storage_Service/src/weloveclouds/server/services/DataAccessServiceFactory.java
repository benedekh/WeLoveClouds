package weloveclouds.server.services;

import weloveclouds.server.services.models.DataAccessServiceInitializationInfo;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.store.MovablePersistentStorage;

public class DataAccessServiceFactory {

    public DataAccessService createServiceWithPersistentStorage(
            DataAccessServiceInitializationInfo initializationContext) {
        return new DataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new KVPersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

    public MovableDataAccessService createServiceWithMovablePersistentStorage(
            DataAccessServiceInitializationInfo initializationContext) {
        return new MovableDataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new MovablePersistentStorage(initializationContext.getStorageRootFolderPath()));
    }



}
