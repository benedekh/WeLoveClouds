package weloveclouds.server.services;

import org.apache.log4j.Logger;

import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.store.MovablePersistentStorage;

/**
 * A factory which produces different types of {@link IDataAccessService} instances.
 * 
 * @author Benedek
 */
public class DataAccessServiceFactory {

    private static final Logger LOGGER = Logger.getLogger(DataAccessServiceFactory.class);

    /**
     * @param initializationContext the parameters for the initialization
     * @return a {@link DataAccessService} which is already initialized by the parameters
     */
    public DataAccessService createInitializedDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        LOGGER.debug("Creating an initialized DataAccessService.");
        return new DataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new KVPersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

    /**
     * @param initializationContext the parameters for the initialization
     * @return a {@link MovableDataAccessService} which is already initialized by the parameters
     */
    public MovableDataAccessService createInitializedMovableDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        LOGGER.debug("Creating an initialized MovableDataAccessService.");
        return new MovableDataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new MovablePersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

    /**
     * @param initializationContext the parameters for the initialization
     * @param replicationTransferer helper class which transfers the replication requests to the
     *        replicas
     * 
     * @return a {@link ReplicableDataAccessService} which is already initialized by the parameters
     */
    public ReplicableDataAccessService createInitializedReplicableDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        LOGGER.debug("Creating an initialized ReplicableDataAccessService.");
        return new ReplicableDataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new MovablePersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

}
