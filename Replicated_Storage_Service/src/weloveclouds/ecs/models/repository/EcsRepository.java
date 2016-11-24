package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.UNSYNCHRONIZED;

/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsRepository {
    List<StorageNode> storageNodes;

    public EcsRepository(List<StorageNode> storageNodes) {
        this.storageNodes = new ArrayList<>(storageNodes);
    }

    public List<StorageNode> getStorageNodes() {
        return storageNodes;
    }

    public void addStorageNode(StorageNode storageNode) {
        this.storageNodes.add(storageNode);
    }

    public int getNumberOfNodes() {
        return storageNodes.size();
    }

    public int getNumberOfNodesWithStatus(StorageNodeStatus status) {
        return getNodesWithStatus(status).size();
    }

    public List<StorageNode> getNodesWithStatus(StorageNodeStatus status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (StorageNode storageNode : getStorageNodes()) {
            if (storageNode.getStatus() == status) {
                nodes.add(storageNode);
            }
        }
        return nodes;
    }

    public List<StorageNode> getNodeWithStatus(List<StorageNodeStatus> status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (StorageNodeStatus nodeStatus : status) {
            nodes.addAll(getNodesWithStatus(nodeStatus));
        }

        return nodes;
    }

    public List<StorageNode> getNodesWithMetadataStatus(StorageNodeStatus status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (StorageNode storageNode : getStorageNodes()) {
            if (storageNode.getMetadataStatus() == status) {
                nodes.add(storageNode);
            }
        }
        return nodes;
    }

    public StorageNode getUnsynchronizedActiveNode() {
        StorageNode unsynchornizedActiveNode = null;

        for (StorageNode node : getNodesWithStatus(INITIALIZED)) {
            if (node.getMetadataStatus() == UNSYNCHRONIZED) {
                unsynchornizedActiveNode = node;
            }
        }
        return unsynchornizedActiveNode;
    }
}
