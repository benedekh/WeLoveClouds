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
    public String toString() {
        return CustomStringJoiner.join(" ", "{ Ring metadata:",
                ringMetadata == null ? null : ringMetadata.toString(), ", Cache size:",
                String.valueOf(cacheSize), ", Displacement strategy:", displacementStrategyName,
                "}");
    }

}
