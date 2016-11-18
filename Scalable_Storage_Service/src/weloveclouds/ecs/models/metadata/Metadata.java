package weloveclouds.ecs.models.metadata;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by Benoit on 2016-11-16.
 */
public class Metadata {
    ArrayDeque<StorageNode> storageNodes;

    public Metadata() {
        this.storageNodes = new ArrayDeque<>();
    }

    public Metadata(ArrayDeque<StorageNode> storageNodes) {
        this.storageNodes = storageNodes;
    }

    public ArrayDeque<StorageNode> getNodes(){
        return storageNodes;
    }

    public void addStorageNode(StorageNode storageNode) {
        this.storageNodes.add(storageNode);
    }
}
