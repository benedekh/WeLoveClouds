package weloveclouds.ecs.models.repository;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-09.
 */
public abstract class AbstractNode {
    protected String name;
    protected ServerConnectionInfo serverConnectionInfo;
    protected ServerConnectionInfo ecsChannelConnectionInfo;
    protected NodeHealthInfos healthInfos;
    protected Hash hashKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
