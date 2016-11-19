package weloveclouds.server.models.requests.kvecs;

import java.nio.file.Path;
import java.nio.file.Paths;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.models.ServerInitializationContext;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.ServiceIsInitializedException;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.MovablePersistentStorage;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;

public class InitializeKVServer implements IKVECSRequest {

    private static final Path PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER = Paths.get("/");

    private IMovableDataAccessService dataAccessService;
    private ServerInitializationContext serverInitializationContext;

    public InitializeKVServer(IMovableDataAccessService dataAccessService,
            ServerInitializationContext initializationContext) {
        this.dataAccessService = dataAccessService;
        this.serverInitializationContext = initializationContext;
    }

    @Override
    public KVAdminMessage execute() {
        String displacementStrategyName = serverInitializationContext.getDisplacementStrategyName();
        DisplacementStrategy displacementStrategy =
                StrategyFactory.createDisplacementStrategy(displacementStrategyName);

        if (displacementStrategy == null) {
            return createErrorKVAdminMessage(CustomStringJoiner.join(": ",
                    "Unknown displacement startegy", displacementStrategyName));
        }

        int cacheSize = serverInitializationContext.getCacheSize();

        try {
            dataAccessService.initializeService(new KVCache(cacheSize, displacementStrategy),
                    new MovablePersistentStorage(PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER));
        } catch (ServiceIsInitializedException ex) {
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
    }

}
