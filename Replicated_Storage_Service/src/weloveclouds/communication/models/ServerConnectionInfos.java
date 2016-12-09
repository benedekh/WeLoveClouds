package weloveclouds.communication.models;

import java.util.Set;

import weloveclouds.communication.models.ServerConnectionInfo;

public class ServerConnectionInfos {

    private Set<ServerConnectionInfo> serverConnectionInfos;

    public ServerConnectionInfos(Set<ServerConnectionInfo> serverConnectionInfos) {
        this.serverConnectionInfos = serverConnectionInfos;
    }

    public Set<ServerConnectionInfo> getServerConnectionInfos() {
        return serverConnectionInfos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((serverConnectionInfos == null) ? 0 : serverConnectionInfos.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ServerConnectionInfos)) {
            return false;
        }
        ServerConnectionInfos other = (ServerConnectionInfos) obj;
        if (serverConnectionInfos == null) {
            if (other.serverConnectionInfos != null) {
                return false;
            }
        } else if (!serverConnectionInfos.equals(other.serverConnectionInfos)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String delimiter = ", ";

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (ServerConnectionInfo connectionInfo : serverConnectionInfos) {
            sb.append(connectionInfo);
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        sb.append("}");

        return sb.toString();
    }

}
