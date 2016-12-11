package weloveclouds.ecs.models.tasks.details;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.hashing.models.RingMetadata;

/**
 * Created by Benoit on 2016-11-30.
 */
public class RemoveNodeTaskDetails {
    private StorageNode nodeToRemove;
    private StorageNode successor;
    private RingMetadata ringMetadata;

    public RemoveNodeTaskDetails(StorageNode nodeToRemove, StorageNode successor, RingMetadata ringMetadata) {
        this.nodeToRemove = nodeToRemove;
        this.successor = successor;
        this.ringMetadata = ringMetadata;
    }

    public StorageNode getNodetoRemove() {
        return nodeToRemove;
    }

    public StorageNode getSuccessor() {
        return successor;
    }

    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }
}
