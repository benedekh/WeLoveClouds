package weloveclouds.hashing.models;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Represents an <IP, port> and <hash-range> triplet, which defines respective server (denoted by
 * its <ip,port>) is responsible for which hash range.
 * 
 * @author Benedek
 */
public class RangeInfo {

    public static String FIELD_DELIMITER = "-\t-";

    private ServerConnectionInfo connectionInfo;
    private HashRange range;

    public RangeInfo(RangeInfoBuilder builder) {
        this.connectionInfo = builder.connectionInfo;
        this.range = builder.range;
    }

    public ServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public boolean rangeContains(Hash hash) {
        return range.contains(hash);
    }

    public String toStringWithDelimiter() {
        return CustomStringJoiner.join(FIELD_DELIMITER, connectionInfo.toString(),
                range.toString());
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "{", "Connection info: ", connectionInfo.toString(),
                ", Range: ", range.toString(), "}");
    }

    public static class RangeInfoBuilder {
        private ServerConnectionInfo connectionInfo;
        private HashRange range;

        public RangeInfoBuilder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public RangeInfoBuilder range(HashRange range) {
            this.range = range;
            return this;
        }

        public RangeInfo build() {
            return new RangeInfo(this);
        }
    }

}
