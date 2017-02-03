package weloveclouds.server.services.datastore;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.encryption.StringEncryptionUtil;
import weloveclouds.server.services.datastore.models.DataAccessServiceInitializationContext;
import weloveclouds.server.services.replication.ReplicationService;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.storage.EncryptedPersistentStorage;
import weloveclouds.server.store.storage.KVPersistentStorage;
import weloveclouds.server.store.storage.MovablePersistentStorage;

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
    public DataAccessService createDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        LOGGER.debug("Creating a DataAccessService.");
        return new DataAccessService(
                new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()),
                new KVPersistentStorage(initializationContext.getStorageRootFolderPath()));
    }

    /**
     * @param initializationContext the parameters for the initialization
     * @return a {@link MovableDataAccessService} which is already initialized by the parameters
     */
    public MovableDataAccessService<?> createMovableDataAccessService(
            DataAccessServiceInitializationContext initializationContext) {
        LOGGER.debug("Creating a MovableDataAccessService.");
        return new MovableDataAccessService.Builder<>()
                .cache(new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()))
                .persistentStorage(new MovablePersistentStorage(
                        initializationContext.getStorageRootFolderPath()))
                .simulatedDataAccessService(new SimulatedMovableDataAccessService()).build();
    }

    /**
     * @param initializationContext the parameters for the initialization
     * @param replicationService the service which is responsible for the replication
     * @return a {@link ReplicableDataAccessService} which is already initialized by the parameters
     */
    public ReplicableDataAccessService createReplicableDataAccessService(
            DataAccessServiceInitializationContext initializationContext,
            ReplicationService replicationService) {
        LOGGER.debug("Creating a ReplicableDataAccessService.");
        return new ReplicableDataAccessService.Builder()
                .cache(new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()))
                .persistentStorage(new MovablePersistentStorage(
                        initializationContext.getStorageRootFolderPath()))
                .simulatedDataAccessService(new SimulatedMovableDataAccessService())
                .replicationService(replicationService).build();
    }

    /**
     * Create a data access service that is backed by an encrypted persistent storage.
     * 
     * @param initializationContext the parameters for the initialization
     * @param replicationService the service which is responsible for the replication
     * @return a {@link ReplicableDataAccessService} which is already initialized by the parameters
     */
    public ReplicableDataAccessService createReplicableDataAccessServiceWithEncryption(
            DataAccessServiceInitializationContext initializationContext,
            ReplicationService replicationService) {
        LOGGER.debug("Creating an encrypted ReplicableDataAccessService.");
        return new ReplicableDataAccessService.Builder()
                .cache(new KVCache(initializationContext.getCacheSize(),
                        initializationContext.getDisplacementStrategy()))
                .persistentStorage(new EncryptedPersistentStorage(
                        initializationContext.getStorageRootFolderPath(),
                        new StringEncryptionUtil()))
                .simulatedDataAccessService(new SimulatedMovableDataAccessService())
                .replicationService(replicationService).build();
    }

}
