package weloveclouds.server.configuration.models;

import java.nio.file.Path;

import weloveclouds.server.client.commands.ServerCommand;
import weloveclouds.server.core.Server;
import weloveclouds.server.services.datastore.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

/**
 * A context object which is handled by the different {@link ServerCommand}s so every configuration
 * parameter of the {@link Server} can be collected at one place.
 * 
 * @author Benedek
 */
public class KVServerCLIContext {

    private boolean isStarted;
    private KVServerPortContext portContext;
    private DataAccessServiceInitializationContext dASInitializationContext;

    public KVServerCLIContext() {
        this.portContext = new KVServerPortContext();
        this.portContext.setKvClientPort(KVServerPortConstants.KVCLIENT_REQUESTS_PORT);
        this.portContext.setKvServerPort(KVServerPortConstants.KVSERVER_REQUESTS_PORT);
        this.portContext.setKvECSPort(KVServerPortConstants.KVECS_REQUESTS_PORT);
        this.dASInitializationContext = new DataAccessServiceInitializationContext();
        this.dASInitializationContext.setCacheSize(-1);
    }

    public KVServerPortContext getPortContext() {
        return portContext;
    }

    public int getClientPort() {
        return portContext.getKVClientPort();
    }

    public int getServerPort() {
        return portContext.getKVServerPort();
    }

    public int getEcsPort() {
        return portContext.getKVECSPort();
    }

    public boolean isStarted() {
        return isStarted;
    }

    public int getCacheSize() {
        return dASInitializationContext.getCacheSize();
    }

    public DisplacementStrategy getDisplacementStrategy() {
        return dASInitializationContext.getDisplacementStrategy();
    }

    public Path getStoragePath() {
        return dASInitializationContext.getStorageRootFolderPath();
    }

    public void setClientPort(int clientPort) {
        portContext.setKvClientPort(clientPort);
    }

    public void setServerPort(int serverPort) {
        portContext.setKvServerPort(serverPort);
    }

    public void setEcsPort(int ecsPort) {
        portContext.setKvECSPort(ecsPort);
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public void setCacheSize(int cacheSize) {
        dASInitializationContext.setCacheSize(cacheSize);
    }

    public void setDisplacementStrategy(DisplacementStrategy displacementStrategy) {
        dASInitializationContext.setDisplacementStrategy(displacementStrategy);
    }

    public void setStoragePath(Path storagePath) {
        dASInitializationContext.setStorageRootFolderPath(storagePath);
    }


}
