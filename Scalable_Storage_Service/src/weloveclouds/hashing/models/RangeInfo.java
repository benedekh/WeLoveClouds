package weloveclouds.hashing.models;

import weloveclouds.communication.models.ServerConnectionInfo;

public class RangeInfo {

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
