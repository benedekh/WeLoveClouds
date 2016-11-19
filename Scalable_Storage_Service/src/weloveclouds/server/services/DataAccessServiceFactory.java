package weloveclouds.server.services;

import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;

public class DataAccessServiceFactory {

    public DataAccessService createInitializedDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        return new DataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new KVPersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

    public MovableDataAccessService createUninitializedMovableDataAccessService() {
        return new MovableDataAccessService();
    }

}
