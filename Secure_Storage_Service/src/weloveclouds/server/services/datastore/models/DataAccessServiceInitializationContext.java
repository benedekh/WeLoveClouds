package weloveclouds.server.services.datastore.models;

import java.nio.file.Path;

import weloveclouds.server.services.datastore.DataAccessService;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.models.PersistedStorageUnit;

/**
 * An initialization context object which stores those parameters that are necessary for the correct
 * behavior of a {@link DataAccessService}. E.g.:<br>
 * (1) size of the cache,<br>
 * (2) displacement strategy to be used in the cache,<br>
 * (3) path for the {@link PersistedStorageUnit} where it stores the entries.
 * 
 * @author Benedek
 */
public class DataAccessServiceInitializationContext {

    private int cacheSize;
    private DisplacementStrategy displacementStrategy;
    private Path storageRootFolderPath;

    public DataAccessServiceInitializationContext() {
        this.cacheSize = 0;
        this.displacementStrategy = null;
        this.storageRootFolderPath = null;
    }

    protected DataAccessServiceInitializationContext(Builder builder) {
        this.cacheSize = builder.cacheSize;
        this.displacementStrategy = builder.displacementStrategy;
        this.storageRootFolderPath = builder.storageRootFolderPath;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public DisplacementStrategy getDisplacementStrategy() {
        return displacementStrategy;
    }

    public Path getStorageRootFolderPath() {
        return storageRootFolderPath;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setDisplacementStrategy(DisplacementStrategy displacementStrategy) {
        this.displacementStrategy = displacementStrategy;
    }

    public void setStorageRootFolderPath(Path storageRootFolderPath) {
        this.storageRootFolderPath = storageRootFolderPath;
    }

    /**
     * A builder to create a {@link DataAccessServiceInitializationContext} instance.
     * 
     * @author Benoit
     */
    public static class Builder {
        private int cacheSize;
        private DisplacementStrategy displacementStrategy;
        private Path storageRootFolderPath;

        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Builder displacementStrategy(DisplacementStrategy displacementStrategy) {
            this.displacementStrategy = displacementStrategy;
            return this;
        }

        public Builder rootFolderPath(Path storageRootFolderPath) {
            this.storageRootFolderPath = storageRootFolderPath;
            return this;
        }

        public DataAccessServiceInitializationContext build() {
            return new DataAccessServiceInitializationContext(this);
        }
    }
}
