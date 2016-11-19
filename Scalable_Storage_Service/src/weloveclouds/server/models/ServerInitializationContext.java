package weloveclouds.server.models;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;

/**
 * The context information with that the ECS initializes the target KVServer.
 * 
 * @author Benedek
 */
public class ServerInitializationContext {

    private RingMetadata ringMetadata;
    private HashRange rangeManagedByServer;
    private int cacheSize;
    private String displacementStrategyName;

    protected ServerInitializationContext(Builder builder) {
        this.ringMetadata = builder.ringMetadata;
        this.rangeManagedByServer = builder.rangeManagedByServer;
        this.cacheSize = builder.cacheSize;
        this.displacementStrategyName = builder.displacementStrategyName;
    }

    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    public HashRange getRangeManagedByServer() {
        return rangeManagedByServer;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public String getDisplacementStrategyName() {
        return displacementStrategyName;
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "{ Ring metadata:",
                ringMetadata == null ? null : ringMetadata.toString(), ", Range managed by server:",
                rangeManagedByServer == null ? null : rangeManagedByServer.toString(),
                ", Cache size:", String.valueOf(cacheSize), ", Displacement strategy:",
                displacementStrategyName, "}");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cacheSize;
        result = prime * result
                + ((displacementStrategyName == null) ? 0 : displacementStrategyName.hashCode());
        result = prime * result
                + ((rangeManagedByServer == null) ? 0 : rangeManagedByServer.hashCode());
        result = prime * result + ((ringMetadata == null) ? 0 : ringMetadata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ServerInitializationContext)) {
            return false;
        }
        ServerInitializationContext other = (ServerInitializationContext) obj;
        if (cacheSize != other.cacheSize) {
            return false;
        }
        if (displacementStrategyName == null) {
            if (other.displacementStrategyName != null) {
                return false;
            }
        } else if (!displacementStrategyName.equals(other.displacementStrategyName)) {
            return false;
        }
        if (rangeManagedByServer == null) {
            if (other.rangeManagedByServer != null) {
                return false;
            }
        } else if (!rangeManagedByServer.equals(other.rangeManagedByServer)) {
            return false;
        }
        if (ringMetadata == null) {
            if (other.ringMetadata != null) {
                return false;
            }
        } else if (!ringMetadata.equals(other.ringMetadata)) {
            return false;
        }
        return true;
    }

    public static class Builder {
        private RingMetadata ringMetadata;
        private HashRange rangeManagedByServer;
        private int cacheSize;
        private String displacementStrategyName;

        public Builder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        public Builder rangeManagedByServer(HashRange rangeManagedByServer) {
            this.rangeManagedByServer = rangeManagedByServer;
            return this;
        }

        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Builder displacementStrategyName(String displacementStrategyName) {
            this.displacementStrategyName = displacementStrategyName;
            return this;
        }

        public ServerInitializationContext build() {
            return new ServerInitializationContext(this);
        }
    }

}
