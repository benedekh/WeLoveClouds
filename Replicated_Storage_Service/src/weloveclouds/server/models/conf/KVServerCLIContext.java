package weloveclouds.server.models.conf;

import java.nio.file.Path;

import weloveclouds.server.core.Server;
import weloveclouds.server.models.commands.ServerCommand;
import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

/**
 * A context object which is handled by the different {@link ServerCommand}s so every configuration
 * parameter of the {@link Server} can be collected at one place.
 * 
 * @author Benedek
 */
public class KVServerCLIContext {

    private boolean isStarted;
    private KVServerPortContext.Builder portContextBuilder;
    private DataAccessServiceInitializationContext.Builder dASInitializationInfoBuilder;

    public KVServerCLIContext() {
        this.portContextBuilder = new KVServerPortContext.Builder();
        this.portContextBuilder.clientPort(KVServerPortConstants.KVCLIENT_REQUESTS_PORT);
        this.portContextBuilder.serverPort(KVServerPortConstants.KVSERVER_REQUESTS_PORT);
        this.portContextBuilder.ecsPort(KVServerPortConstants.KVECS_REQUESTS_PORT);
        this.dASInitializationInfoBuilder = new DataAccessServiceInitializationContext.Builder();
        this.dASInitializationInfoBuilder.cacheSize(-1);
    }

    public KVServerPortContext getPortContext() {
        return portContextBuilder.build();
    }

    public int getClientPort() {
        return portContextBuilder.build().getKVClientPort();
    }

    public int getServerPort() {
        return portContextBuilder.build().getKVServerPort();
    }

    public int getEcsPort() {
        return portContextBuilder.build().getKVECSPort();
    }

    public boolean isStarted() {
        return isStarted;
    }

    public int getCacheSize() {
        return dASInitializationInfoBuilder.build().getCacheSize();
    }

    public DisplacementStrategy getDisplacementStrategy() {
        return dASInitializationInfoBuilder.build().getDisplacementStrategy();
    }

    public Path getStoragePath() {
        return dASInitializationInfoBuilder.build().getStorageRootFolderPath();
    }

    public void setClientPort(int clientPort) {
        portContextBuilder.clientPort(clientPort);
    }

    public void setServerPort(int serverPort) {
        portContextBuilder.serverPort(serverPort);
    }

    public void setEcsPort(int ecsPort) {
        portContextBuilder.ecsPort(ecsPort);
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public void setCacheSize(int cacheSize) {
        dASInitializationInfoBuilder.cacheSize(cacheSize);
    }

    public void setDisplacementStrategy(DisplacementStrategy displacementStrategy) {
        dASInitializationInfoBuilder.displacementStrategy(displacementStrategy);
    }

    public void setStoragePath(Path storagePath) {
        dASInitializationInfoBuilder.rootFolderPath(storagePath);
    }


}
