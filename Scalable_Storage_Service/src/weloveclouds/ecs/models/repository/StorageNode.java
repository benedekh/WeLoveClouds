package weloveclouds.ecs.models.repository;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode {
    private String id;
    private StorageNodeStatus status;
    private ServerConnectionInfo serverConnectionInfo;
    private HashRange hashRange;

    public StorageNode(String id, ServerConnectionInfo serverConnectionInfo, HashRange hashRange) {
        this.id = id;
        this.serverConnectionInfo = serverConnectionInfo;
        this.hashRange = hashRange;
        this.status = IDLE;
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
        this.hashRange = hashRange;
    }

    public StorageNodeStatus getStatus() {
        return status;
    }

    public void setStatus(StorageNodeStatus status) {
        this.status = status;
    }

    public String toString() {
        return CustomStringJoiner.join(" ", "Node:" + getIpAddress() + "Status:" + status.name());
    }
}
