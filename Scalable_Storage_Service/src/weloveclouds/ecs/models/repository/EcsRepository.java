package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsRepository {
    List<StorageNode> storageNodes;

    public EcsRepository(List<StorageNode> storageNodes) {
        this.storageNodes = new ArrayList<>(storageNodes);
    }

    synchronized public List<StorageNode> getStorageNodes() {
        return storageNodes;
    }

    synchronized public void addStorageNode(StorageNode storageNode) {
        this.storageNodes.add(storageNode);
    }

    synchronized public int getNumberOfNodes() {
        return storageNodes.size();
    }

    synchronized public int getNumberOfNodesWithStatus(StorageNodeStatus status) {
        return getNodesWithStatus(status).size();
    }

    synchronized public List<StorageNode> getNodesWithStatus(StorageNodeStatus status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (StorageNode storageNode : getStorageNodes()) {
            if (storageNode.getStatus() == status) {
                nodes.add(storageNode);
            }
        }
        return nodes;
    }

    synchronized public List<StorageNode> getNodeWithStatus(List<StorageNodeStatus> status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (StorageNodeStatus nodeStatus : status) {
            nodes.addAll(getNodesWithStatus(nodeStatus));
        }

        return nodes;
    }

}
