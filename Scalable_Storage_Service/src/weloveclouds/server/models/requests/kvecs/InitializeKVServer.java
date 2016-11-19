package weloveclouds.server.models.requests.kvecs;

import java.nio.file.Path;
import java.nio.file.Paths;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.models.ServerInitializationContext;
import weloveclouds.server.services.DataAccessServiceFactory;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.models.DataAccessServiceInitializationInfo;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;

public class InitializeKVServer implements IKVECSRequest {

    private static final Path PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER = Paths.get("/");

    private DataAccessServiceFactory dataAccessServiceFactory;
    private IMovableDataAccessService dataAccessService;

    private ServerInitializationContext serverInitializationContext;

    public InitializeKVServer(DataAccessServiceFactory dataAccessServiceFactory,
            IMovableDataAccessService dataAccessService,
            ServerInitializationContext initializationContext) {
        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.dataAccessService = dataAccessService;
        this.serverInitializationContext = initializationContext;
    }

    @Override
    public KVAdminMessage execute() {
        String displacementStrategyName = serverInitializationContext.getDisplacementStrategyName();
        DisplacementStrategy displacementStrategy =
                StrategyFactory.createDisplacementStrategy(displacementStrategyName);

        if (displacementStrategy == null) {
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage(CustomStringJoiner.join(": ", "Unknown displacement startegy",
                            displacementStrategyName))
                    .build();
        }

        int cacheSize = serverInitializationContext.getCacheSize();
        DataAccessServiceInitializationInfo dataAccessServiceInitializationInfo =
                new DataAccessServiceInitializationInfo.Builder().cacheSize(cacheSize)
                        .displacementStrategy(displacementStrategy)
                        .rootFolderPath(PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER).build();

        dataAccessService = dataAccessServiceFactory
                .createServiceWithMovablePersistentStorage(dataAccessServiceInitializationInfo);

        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

}
