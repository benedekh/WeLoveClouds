package weloveclouds.loadbalancer.models;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2016-12-05.
 */
public class NodeHealthInfos implements Comparable<NodeHealthInfos> {
    ServerConnectionInfo serverConnectionInfo;
    String serverName;
    int numberOfActiveConnections;

    public NodeHealthInfos(String serverName, ServerConnectionInfo serverConnectionInfo, int
            numberOfActiveConnections) {
        this.serverName = serverName;
        this.serverConnectionInfo = serverConnectionInfo;
        this.numberOfActiveConnections = numberOfActiveConnections;
    }

    public ServerConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }

    public void setServerConnectionInfo(ServerConnectionInfo serverConnectionInfo) {
        this.serverConnectionInfo = serverConnectionInfo;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getNumberOfActiveConnections() {
        return numberOfActiveConnections;
    }

    public void setNumberOfActiveConnections(int numberOfActiveConnections) {
        this.numberOfActiveConnections = numberOfActiveConnections;
    }

    @Override
    public int compareTo(NodeHealthInfos otherNodeHealtInfos) {
        if (numberOfActiveConnections == otherNodeHealtInfos.getNumberOfActiveConnections())
            return 0;
        else if (numberOfActiveConnections > otherNodeHealtInfos.getNumberOfActiveConnections())
            return 1;
        else
            return -1;
    }
}
