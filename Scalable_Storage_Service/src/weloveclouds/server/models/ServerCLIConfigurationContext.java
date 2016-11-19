package weloveclouds.server.models;

import java.nio.file.Path;

import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

/**
 * A context object which is handled by the different {@link ServerCommand}s so every configuration
 * parameter of the {@link Server} can be collected at one place.
 * 
 * @author Benedek
 */
public class ServerCLIConfigurationContext {

    private int port;
    private int cacheSize;
    private DisplacementStrategy displacementStrategy;
    private Path storagePath;
    private boolean isStarted;

    public ServerCLIConfigurationContext() {
        this.port = -1;
        this.cacheSize = -1;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public DisplacementStrategy getDisplacementStrategy() {
        return displacementStrategy;
    }

    public void setDisplacementStrategy(DisplacementStrategy displacementStrategy) {
        this.displacementStrategy = displacementStrategy;
    }

    public Path getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(Path storagePath) {
        this.storagePath = storagePath;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }



}
