package weloveclouds.ecs.models.metadata;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode {
    private String Id;
    private ServerConnectionInfo serverConnectionInfo;
    private HashRange hashRange;

    public StorageNode(String id, ServerConnectionInfo serverConnectionInfo, HashRange hashRange) {
        Id = id;
        this.serverConnectionInfo = serverConnectionInfo;
        this.hashRange = hashRange;
    }

    public String getId() {
        return Id;
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
}
