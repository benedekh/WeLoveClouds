package weloveclouds.server.services;

import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;

/**
 * A factory which produces different types of {@link IDataAccessService} instances.
 * 
 * @author Benedek
 */
public class DataAccessServiceFactory {

    /**
     * @param initializationContext the parameters for the initialization
     * @return a {@link DataAccessService} which is already initialized by the parameters
     */
    public DataAccessService createInitializedDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        return new DataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new KVPersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

    /**
     * @return a {@link MovableDataAccessService} which has to be initialized before it can be used
     */
    public MovableDataAccessService createUninitializedMovableDataAccessService() {
        return new MovableDataAccessService();
    }

}
