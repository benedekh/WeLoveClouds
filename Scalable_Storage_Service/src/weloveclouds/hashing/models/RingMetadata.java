package weloveclouds.hashing.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores <IP, port> and <hash-range> triplets, which defines respective server (denoted by its
 * <ip,port>) is responsible for which hash range.
 * 
 * @author Benedek
 */
public class RingMetadata {

    private Set<RingMetadataPart> metadataParts;

    public RingMetadata() {
        this.metadataParts = new HashSet<>();
    }

    public RingMetadata(Set<RingMetadataPart> rangeInfos) {
        this.metadataParts = rangeInfos;
    }

    public void addRangeInfo(RingMetadataPart info) {
        metadataParts.add(info);
    }

    public void removeRangeInfo(RingMetadataPart info) {
        metadataParts.remove(info);
    }

    /**
     * Get that server's details which handles the responsible hash value.
     * 
     * @return the range information (IP+port+range) of that server which handles the respective
     *         hash value
     */
    public RingMetadataPart findServerInfoByHash(Hash hash) {
        for (RingMetadataPart metadataPart : metadataParts) {
            if (metadataPart.rangeContains(hash)) {
                return metadataPart;
            }
        }
        return null;
    }

    public String toStringWithDelimiter(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (RingMetadataPart metadataPart : metadataParts) {
            sb.append(metadataPart);
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(toStringWithDelimiter(", "));
        sb.append("}");
        return sb.toString();
    }

}
