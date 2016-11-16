package weloveclouds.hashing.models;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Represents an <IP, port> and <hash-range> triplet, which defines respective server (denoted by
 * its <ip,port>) is responsible for which hash range.
 * 
 * @author Benedek
 */
public class RingMetadataPart {

    private ServerConnectionInfo connectionInfo;
    private HashRange range;

    public RingMetadataPart(RingMetadataPartBuilder builder) {
        this.connectionInfo = builder.connectionInfo;
        this.range = builder.range;
    }

    public ServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }
    
    public HashRange getRange(){
        return range;
    }

    public boolean rangeContains(Hash hash) {
        return range.contains(hash);
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "{", "Connection info: ", connectionInfo.toString(),
                ", Range: ", range.toString(), "}");
    }

    public static class RingMetadataPartBuilder {
        private ServerConnectionInfo connectionInfo;
        private HashRange range;

        public RingMetadataPartBuilder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public RingMetadataPartBuilder range(HashRange range) {
            this.range = range;
            return this;
        }

        public RingMetadataPart build() {
            return new RingMetadataPart(this);
        }
    }

}
