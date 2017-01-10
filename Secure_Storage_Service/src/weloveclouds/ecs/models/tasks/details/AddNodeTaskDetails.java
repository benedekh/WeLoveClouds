package weloveclouds.ecs.models.tasks.details;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.hashing.models.RingMetadata;

/**
 * Created by Benoit on 2016-11-30.
 */
public class AddNodeTaskDetails {
    private StorageNode newStorageNode;
    private StorageNode successor;
    private RingMetadata ringMetadata;
    private String displacementStrategy;
    private int cacheSize;

    public AddNodeTaskDetails(StorageNode newStorageNode, StorageNode successor,
                              RingMetadata ringMetadata, String displacementStrategy, int cacheSize) {
        this.newStorageNode = newStorageNode;
        this.successor = successor;
        this.ringMetadata = ringMetadata;
        this.displacementStrategy = displacementStrategy;
        this.cacheSize = cacheSize;
    }

    public StorageNode getNewStorageNode() {
        return newStorageNode;
    }

    public StorageNode getSuccessor() {
        return successor;
    }

    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    public String getDisplacementStrategy() {
        return displacementStrategy;
    }

    public int getCacheSize() {
        return cacheSize;
    }
}
