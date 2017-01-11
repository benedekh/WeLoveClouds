package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.utils.ListUtils;

import static weloveclouds.ecs.models.repository.NodeStatus.IDLE;
import static weloveclouds.ecs.models.repository.NodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.NodeStatus.UNSYNCHRONIZED;

/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsRepository {
    Loadbalancer loadbalancer;
    List<StorageNode> storageNodes;

    public EcsRepository(List<StorageNode> storageNodes) {
        this.storageNodes = new ArrayList<>(storageNodes);
        initRepository();
    }

    private void initRepository() {
        StorageNode loadbalancerElect = (StorageNode) ListUtils.getRandomObjectFrom
                (getNodesWithStatus(IDLE));
        this.storageNodes.remove(loadbalancerElect);
        this.loadbalancer = new Loadbalancer(loadbalancerElect);
    }

    public Loadbalancer getLoadbalancer() {
        return this.loadbalancer;
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

    public int getNumberOfNodesWithStatus(NodeStatus status) {
        return getNodesWithStatus(status).size();
    }

    public List<StorageNode> getNodesWithStatus(NodeStatus status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (StorageNode storageNode : getStorageNodes()) {
            if (storageNode.getStatus() == status) {
                nodes.add(storageNode);
            }
        }
        return nodes;
    }

    public List<StorageNode> getNodeWithStatus(List<NodeStatus> status) {
        List<StorageNode> nodes = new ArrayList<>();

        for (NodeStatus nodeStatus : status) {
            nodes.addAll(getNodesWithStatus(nodeStatus));
        }

        return nodes;
    }

    public List<StorageNode> getNodesWithMetadataStatus(NodeStatus status) {
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
