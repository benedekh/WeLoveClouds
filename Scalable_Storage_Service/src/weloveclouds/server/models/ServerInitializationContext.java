package weloveclouds.server.models;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * The context information with that the ECS initializes the target KVServer's data access service.
 * 
 * @author Benedek
 */
public class ServerInitializationContext {

    private int cacheSize;
    private String displacementStrategyName;

    protected ServerInitializationContext(Builder builder) {
        this.cacheSize = builder.cacheSize;
        this.displacementStrategyName = builder.displacementStrategyName;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public String getDisplacementStrategyName() {
        return displacementStrategyName;
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "{ Cache size:", String.valueOf(cacheSize),
                ", Displacement strategy:", displacementStrategyName, "}");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cacheSize;
        result = prime * result
                + ((displacementStrategyName == null) ? 0 : displacementStrategyName.hashCode());
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
        return true;
    }

    public static class Builder {
        private int cacheSize;
        private String displacementStrategyName;

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
