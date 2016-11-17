package weloveclouds.server.models;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;

/**
 * The context information with that the ECS initializes the target KVServer.
 * 
 * @author Benedek
 */
public class ServerInitializationContext {

    private RingMetadata ringMetadata;
    private int cacheSize;
    private String displacementStrategyName;

    public ServerInitializationContext(RingMetadata ringMetadata, int cacheSize,
            String displacementStrategyName) {
        this.ringMetadata = ringMetadata;
        this.cacheSize = cacheSize;
        this.displacementStrategyName = displacementStrategyName;
    }

    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public String getDisplacementStrategyName() {
        return displacementStrategyName;
    }
    
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cacheSize;
        result = prime * result
                + ((displacementStrategyName == null) ? 0 : displacementStrategyName.hashCode());
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
        if (ringMetadata == null) {
            if (other.ringMetadata != null) {
                return false;
            }
        } else if (!ringMetadata.equals(other.ringMetadata)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "{ Ring metadata:",
                ringMetadata == null ? null : ringMetadata.toString(), ", Cache size:",
                String.valueOf(cacheSize), ", Displacement strategy:", displacementStrategyName,
                "}");
    }

}
