package weloveclouds.ecs.models.repository;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-09.
 */
public abstract class AbstractNode {
    protected String name;
    protected NodeStatus status;
    protected ServerConnectionInfo serverConnectionInfo;
    protected ServerConnectionInfo ecsChannelConnectionInfo;
    protected NodeHealthInfos healthInfos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
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

    public String getIpAddress() {
        return serverConnectionInfo.getIpAddress().toString().replace("/", "");
    }

    public int getPort() {
        return serverConnectionInfo.getPort();
    }
}
