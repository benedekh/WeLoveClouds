package weloveclouds.commons.hashing.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores <IP, port> and <hash-ranges> triplets, which defines respective server (denoted by its
 * <ip,port>) is responsible for which hash range with what role.
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
        metadataParts.remove(info);
        metadataParts.add(info);
    }

    public void removeRangeInfo(RingMetadataPart info) {
        metadataParts.remove(info);
    }

    public Set<RingMetadataPart> getMetadataParts() {
        return Collections.unmodifiableSet(metadataParts);
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

    @Override
    public String toString() {
        String delimiter = ", ";

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (RingMetadataPart metadataPart : metadataParts) {
            sb.append(metadataPart);
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        sb.append("}");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((metadataParts == null) ? 0 : metadataParts.hashCode());
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
        if (!(obj instanceof RingMetadata)) {
            return false;
        }
        RingMetadata other = (RingMetadata) obj;
        if (metadataParts == null) {
            if (other.metadataParts != null) {
                return false;
            }
        } else if (!metadataParts.equals(other.metadataParts)) {
            return false;
        }
        return true;
    }

}
