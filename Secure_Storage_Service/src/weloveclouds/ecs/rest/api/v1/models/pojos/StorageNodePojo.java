package weloveclouds.ecs.rest.api.v1.models.pojos;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2017-01-24.
 */
public class StorageNodePojo {
    private String name;
    private NodeStatus status;
    private NodeStatus metadataStatus;
    private HashRange hashRange;
    private List<String> replicas = new ArrayList<>();
    private List<HashRange> readRanges;
    private ServerConnectionInfo connectionInfo;
    private Hash hashKey;

    public StorageNodePojo(StorageNode storageNode) {
        this.name = storageNode.getName();
        this.status = storageNode.getStatus();
        this.metadataStatus = storageNode.getMetadataStatus();
        this.hashRange = storageNode.getHashRange();
        for (StorageNode node : storageNode.getReplicas()) {
            replicas.add(node.getName());
        }
        this.readRanges = storageNode.getReadRanges();
        this.connectionInfo = storageNode.getServerConnectionInfo();
        this.hashKey = storageNode.getHashKey();
    }

    public String getName() {
        return name;
    }

    public NodeStatus getMetadataStatus() {
        return metadataStatus;
    }

    public HashRange getHashRange() {
        return hashRange;
    }

    public List<String> getReplicas() {
        return replicas;
    }

    public List<HashRange> getReadRanges() {
        return readRanges;
    }

    public ServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public Hash getHashKey() {
        return hashKey;
    }

    public NodeStatus getStatus() {
        return status;
    }
}
