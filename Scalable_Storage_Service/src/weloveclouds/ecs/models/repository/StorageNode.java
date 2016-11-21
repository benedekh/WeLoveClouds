package weloveclouds.ecs.models.repository;

import java.math.BigInteger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode {
    private String id;
    private StorageNodeStatus status;
    private ServerConnectionInfo serverConnectionInfo;
    private Hash hashKey;
    private HashRange hashRange;

    public StorageNode(String id, ServerConnectionInfo serverConnectionInfo) {
        this.id = id;
        this.serverConnectionInfo = serverConnectionInfo;
        this.status = IDLE;
        this.hashKey = HashingUtil.getHash(serverConnectionInfo.toString());
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
        this.hashRange = hashRange;
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
