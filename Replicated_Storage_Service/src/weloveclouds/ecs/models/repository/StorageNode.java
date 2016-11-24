package weloveclouds.ecs.models.repository;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;

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
