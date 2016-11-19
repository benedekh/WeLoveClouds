package weloveclouds.server.services.models;

import java.nio.file.Path;

import weloveclouds.server.store.cache.strategy.DisplacementStrategy;

public class DataAccessServiceInitializationContext {

    private int cacheSize;
    private DisplacementStrategy displacementStrategy;
    private Path storageRootFolderPath;

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
