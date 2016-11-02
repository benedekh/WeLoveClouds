package weloveclouds.server.models;

import java.nio.file.Path;

import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

public class ServerConfigurationContext {

    private int port;
    private int cacheSize;
    private DisplacementStrategy displacementStrategy;
    private Path storagePath;
    private boolean isStarted;

    public ServerConfigurationContext() {
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
