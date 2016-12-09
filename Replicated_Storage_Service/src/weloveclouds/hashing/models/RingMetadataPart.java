package weloveclouds.hashing.models;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.Role;


/**
 * Represents an <IP, port> and <hash-range with role> triplet, which defines respective server
 * (denoted by its <ip,port>) is responsible for which hash range with what role.
 * 
 * @author Benedek
 */
public class RingMetadataPart {

    private ServerConnectionInfo connectionInfo;
    private HashRangeWithRole rangeWithRole;

    protected RingMetadataPart(Builder builder) {
        this.connectionInfo = builder.connectionInfo;
        this.rangeWithRole = builder.rangeWithRole;
    }

    public ServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public HashRange getRange() {
        return rangeWithRole.getHashRange();
    }

    public Role getRole() {
        return rangeWithRole.getRole();
    }

    public HashRangeWithRole getRangeWithRole() {
        return rangeWithRole;
    }

    /**
     * @return true if the range contains the referred hash
     */
    public boolean rangeContains(Hash hash) {
        return rangeWithRole.getHashRange().contains(hash);
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "{", "Connection info: ", connectionInfo.toString(),
                ", Range with role: ", rangeWithRole.toString(), "}");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionInfo == null) ? 0 : connectionInfo.hashCode());
        result = prime * result + ((rangeWithRole == null) ? 0 : rangeWithRole.hashCode());
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
        if (!(obj instanceof RingMetadataPart)) {
            return false;
        }
        RingMetadataPart other = (RingMetadataPart) obj;
        if (connectionInfo == null) {
            if (other.connectionInfo != null) {
                return false;
            }
        } else if (!connectionInfo.equals(other.connectionInfo)) {
            return false;
        }
        if (rangeWithRole == null) {
            if (other.rangeWithRole != null) {
                return false;
            }
        } else if (!rangeWithRole.equals(other.rangeWithRole)) {
            return false;
        }
        return true;
    }

    /**
     * Builder pattern for creating a {@link RingMetadataPart} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ServerConnectionInfo connectionInfo;
        private HashRangeWithRole rangeWithRole;

        public Builder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder rangeWithRole(HashRangeWithRole rangeWithRole) {
            this.rangeWithRole = rangeWithRole;
            return this;
        }

        public RingMetadataPart build() {
            return new RingMetadataPart(this);
        }
    }

}
