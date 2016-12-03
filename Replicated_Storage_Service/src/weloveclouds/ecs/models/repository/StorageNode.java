package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtil;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode {
    private String id;
    private StorageNodeStatus metadataStatus;
    private StorageNodeStatus status;
    private ServerConnectionInfo serverConnectionInfo;
    private ServerConnectionInfo ecsChannelConnectionInfo;
    private Hash hashKey;
    private HashRange previousHashRange;
    private HashRange hashRange;
    private List<StorageNode> replicas;

    public StorageNode(String id, ServerConnectionInfo serverConnectionInfo) {
        this.id = id;
        this.serverConnectionInfo = serverConnectionInfo;
        this.status = IDLE;
        this.metadataStatus = UNSYNCHRONIZED;
        this.hashKey = HashingUtil.getHash(serverConnectionInfo.toString());
        this.ecsChannelConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(serverConnectionInfo.getIpAddress())
                .port(ExternalConfigurationServiceConstants.ECS_REQUESTS_PORT)
                .build();
        this.replicas = new ArrayList<>();
    }

    public void addReplicas(StorageNode storageNode) {
        replicas.add(storageNode);
    }

    public void removeReplicas(StorageNode storageNode) {
        replicas.remove(storageNode);
    }

    public List<StorageNode> getReplicas() {
        return new ArrayList<>(replicas);
    }

    public ServerConnectionInfo getEcsChannelConnectionInfo() {
        return ecsChannelConnectionInfo;
    }

    public Hash getHashKey() {
        return hashKey;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return serverConnectionInfo.getIpAddress().toString();
    }

    public int getPort() {
        return serverConnectionInfo.getPort();
    }

    public HashRange getHashRange() {
        return hashRange;
    }

    public void setHashRange(HashRange hashRange) {
        this.previousHashRange = hashRange;
        this.hashRange = hashRange;
        this.metadataStatus = SYNCHRONIZED;
    }

    public StorageNodeStatus getMetadataStatus() {
        return metadataStatus;
    }

    public void setMetadataStatus(StorageNodeStatus metadataStatus) {
        this.metadataStatus = metadataStatus;
    }

    public StorageNodeStatus getStatus() {
        return status;
    }

    public void setStatus(StorageNodeStatus status) {
        this.status = status;
    }

    public ServerConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }

    public String toString() {
        return CustomStringJoiner.join(" ", "Node:" + getIpAddress() + "Status:" + status.name());
    }
}
