package weloveclouds.loadbalancer.models;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2016-12-05.
 */
public class NodeHealthInfos implements Comparable<NodeHealthInfos> {
    private String serverName;
    private ServerConnectionInfo serverConnectionInfo;
    private int numberOfActiveConnections;

    protected NodeHealthInfos(Builder nodeHealthInfosBuilder) {
        this.serverName = nodeHealthInfosBuilder.serverName;
        this.serverConnectionInfo = nodeHealthInfosBuilder.serverConnectionInfo;
        this.numberOfActiveConnections = nodeHealthInfosBuilder.numberOfActiveConnections;
    }

    public ServerConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }

    public String getServerName() {
        return serverName;
    }

    public int getNumberOfActiveConnections() {
        return numberOfActiveConnections;
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

    public static class Builder {
        private ServerConnectionInfo serverConnectionInfo;
        private String serverName;
        private int numberOfActiveConnections;

        public Builder serverConnectionInfo(ServerConnectionInfo serverConnectionInfo) {
            this.serverConnectionInfo = serverConnectionInfo;
            return this;
        }

        public Builder numberOfActiveConnections(int numberOfActiveConnections) {
            this.numberOfActiveConnections = numberOfActiveConnections;
            return this;
        }

        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public NodeHealthInfos build() {
            return new NodeHealthInfos(this);
        }
    }
}
