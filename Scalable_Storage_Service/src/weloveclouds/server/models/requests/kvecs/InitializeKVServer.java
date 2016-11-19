package weloveclouds.server.models.requests.kvecs;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.models.ServerInitializationContext;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.ServiceIsAlreadyInitializedException;
import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;

/**
 * An initialization request to the {@link IMovableDataAccessService}, which initializes the
 * service.
 * 
 * @author Benedek
 */
public class InitializeKVServer implements IKVECSRequest {

    private static final Path PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER = Paths.get("/");
    private static final Logger LOGGER = Logger.getLogger(InitializeKVServer.class);

    private IMovableDataAccessService dataAccessService;
    private ServerInitializationContext serverInitializationContext;

    /**
     * @param dataAccessService which is used for the data access
     * @param initializationContext the context object which contains the initialization parameters
     *        for the data access service
     */
    public InitializeKVServer(IMovableDataAccessService dataAccessService,
            ServerInitializationContext initializationContext) {
        this.dataAccessService = dataAccessService;
        this.serverInitializationContext = initializationContext;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing initialize KVServer request.");

        int cacheSize = serverInitializationContext.getCacheSize();

        String displacementStrategyName = serverInitializationContext.getDisplacementStrategyName();
        DisplacementStrategy displacementStrategy =
                StrategyFactory.createDisplacementStrategy(displacementStrategyName);
        if (displacementStrategy == null) {
            String errorMessage = CustomStringJoiner.join(": ", "Unknown displacement startegy",
                    displacementStrategyName);
            LOGGER.error(errorMessage);
            return createErrorKVAdminMessage(errorMessage);
        }

        DataAccessServiceInitializationContext initializationInfo =
                new DataAccessServiceInitializationContext.Builder().cacheSize(cacheSize)
                        .displacementStrategy(displacementStrategy)
                        .rootFolderPath(PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER).build();
        try {
            dataAccessService.initializeService(initializationInfo);
        } catch (ServiceIsAlreadyInitializedException ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        LOGGER.debug("Initialization finished successfully.");
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
    }

}
