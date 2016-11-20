package weloveclouds.ecs.models.repository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ServerRepository {
    ArrayDeque<StorageNode> storageNodes;

    public ServerRepository() {
        this.storageNodes = new ArrayDeque<>();
    }

    public ServerRepository(ArrayDeque<StorageNode> storageNodes) {
        this.storageNodes = storageNodes;
    }

    synchronized public ArrayDeque<StorageNode> getStorageNodes() {
        return storageNodes;
    }

    synchronized public void addStorageNode(StorageNode storageNode) {
        this.storageNodes.add(storageNode);
    }

    synchronized public List<StorageNode> getIdledNodes() {
        List<StorageNode> idledStorageNodes = new ArrayList<>();

        for (StorageNode storageNode : getStorageNodes()) {
            if (storageNode.getStatus() == IDLE) {
                idledStorageNodes.add(storageNode);
            }
        }
        return idledStorageNodes;
    }
}
