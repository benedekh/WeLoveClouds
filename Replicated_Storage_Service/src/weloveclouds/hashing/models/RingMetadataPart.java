package weloveclouds.hashing.models;

import java.util.Set;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.utils.SetToStringUtility;


/**
 * Represents an <IP, port> and ranges for which it has READ and WRITE privilege, which defines
 * respective server (denoted by its <ip,port>) is responsible for which hash range with what role.
 * 
 * @author Benedek
 */
public class RingMetadataPart {

    private ServerConnectionInfo connectionInfo;
    private Set<HashRange> readRanges;
    private HashRange writeRange;

    protected RingMetadataPart(Builder builder) {
        this.connectionInfo = builder.connectionInfo;
        this.readRanges = builder.readRanges;
        this.writeRange = builder.writeRange;
    }

    public ServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public HashRange getWriteRange() {
        return writeRange;
    }

    public Set<HashRange> getReadRanges() {
        return readRanges;
    }

    /**
     * @return true if the write range contains the referred hash
     */
    public boolean rangeContains(Hash hash) {
        return writeRange.contains(hash);
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "{", "Connection info: ", connectionInfo.toString(),
                ", Write range: ", writeRange == null ? null : writeRange.toString(),
                ", Read ranges: ", SetToStringUtility.toString(readRanges), "}");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionInfo == null) ? 0 : connectionInfo.hashCode());
        result = prime * result + ((readRanges == null) ? 0 : readRanges.hashCode());
        result = prime * result + ((writeRange == null) ? 0 : writeRange.hashCode());
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
        if (readRanges == null) {
            if (other.readRanges != null) {
                return false;
            }
        } else if (!readRanges.equals(other.readRanges)) {
            return false;
        }
        if (writeRange == null) {
            if (other.writeRange != null) {
                return false;
            }
        } else if (!writeRange.equals(other.writeRange)) {
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
        private Set<HashRange> readRanges;
        private HashRange writeRange;

        public Builder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder readRanges(Set<HashRange> readRanges) {
            this.readRanges = readRanges;
            return this;
        }

        public Builder writeRange(HashRange writeRange) {
            this.writeRange = writeRange;
            return this;
        }

        public RingMetadataPart build() {
            return new RingMetadataPart(this);
        }
    }

}
