package weloveclouds.ecs.models.repository;

import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-09.
 */
public abstract class AbstractNode {
    protected String id;
    protected ServerConnectionInfo serverConnectionInfo;
    protected ServerConnectionInfo ecsChannelConnectionInfo;
    protected NodeHealthInfos healthInfos;
    protected Hash hashKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServerConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }

    public ServerConnectionInfo getEcsChannelConnectionInfo() {
        return ecsChannelConnectionInfo;
    }

    public NodeHealthInfos getHealthInfos() {
        return healthInfos;
    }

    public Hash getHashKey() {
        return hashKey;
    }

    public String getIpAddress() {
        return serverConnectionInfo.getIpAddress().toString();
    }

    public int getPort() {
        return serverConnectionInfo.getPort();
    }
}
