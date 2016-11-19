package weloveclouds.server.models;

import java.nio.file.Path;

import weloveclouds.server.core.Server;
import weloveclouds.server.models.commands.ServerCommand;
import weloveclouds.server.services.models.DataAccessServiceInitializationInfo;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

/**
 * A context object which is handled by the different {@link ServerCommand}s so every configuration
 * parameter of the {@link Server} can be collected at one place.
 * 
 * @author Benedek
 */
public class ServerCLIConfigurationContext {

    private int port;
    private boolean isStarted;

    private DataAccessServiceInitializationInfo.Builder initializationInfoBuilder;

    public ServerCLIConfigurationContext() {
        this.port = -1;
        this.initializationInfoBuilder = new DataAccessServiceInitializationInfo.Builder();
        this.initializationInfoBuilder.cacheSize(-1);
    }

    public int getPort() {
        return port;
    }

    public int getCacheSize() {
        return initializationInfoBuilder.build().getCacheSize();
    }

    public DisplacementStrategy getDisplacementStrategy() {
        return initializationInfoBuilder.build().getDisplacementStrategy();
    }

    public Path getStoragePath() {
        return initializationInfoBuilder.build().getStorageRootFolderPath();
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCacheSize(int cacheSize) {
        initializationInfoBuilder.cacheSize(cacheSize);
    }

    public void setDisplacementStrategy(DisplacementStrategy displacementStrategy) {
        initializationInfoBuilder.displacementStrategy(displacementStrategy);
    }

    public void setStoragePath(Path storagePath) {
        initializationInfoBuilder.rootFolderPath(storagePath);
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

}
