package weloveclouds.hashing.models;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.serialization.helper.ISerializer;

/**
 * Represents an <IP, port> and <hash-range> triplet, which defines respective server (denoted by
 * its <ip,port>) is responsible for which hash range.
 * 
 * @author Benedek
 */
public class RingMetadataPart {

    private ServerConnectionInfo connectionInfo;
    private HashRange range;

    protected RingMetadataPart(Builder builder) {
        this.connectionInfo = builder.connectionInfo;
        this.range = builder.range;
    }

    public ServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public HashRange getRange() {
        return range;
    }

    /**
     * @return true if the {@link #range} contains the referred hash.
     */
    public boolean rangeContains(Hash hash) {
        return range.contains(hash);
    }


    /**
     * Converts the object to String.
     * 
     * @param delimiter separator character among the fields
     * @param connectionInfoSerializer to convert the {@link ServerConnectionInfo} into a String
     *        representation
     * @param hashRangeSerializer to convert the {@link HashRange} into a String representation
     */
    public String toStringWithDelimiter(String delimiter,
            ISerializer<String, ServerConnectionInfo> connectionInfoSerializer,
            ISerializer<String, HashRange> hashRangeSerializer) {
        return CustomStringJoiner.join(delimiter,
                connectionInfoSerializer.serialize(connectionInfo),
                hashRangeSerializer.serialize(range));
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "{", "Connection info: ", connectionInfo.toString(),
                ", Range: ", range.toString(), "}");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionInfo == null) ? 0 : connectionInfo.hashCode());
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
        return true;
    }

    /**
     * Builder pattern for creating a {@link RingMetadataPart} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ServerConnectionInfo connectionInfo;
        private HashRange range;

        public Builder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder range(HashRange range) {
            this.range = range;
            return this;
        }

        public RingMetadataPart build() {
            return new RingMetadataPart(this);
        }
    }

}
