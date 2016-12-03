package weloveclouds.commons.hashing.models;

import java.util.HashSet;
import java.util.Set;

import weloveclouds.kvstore.serialization.helper.ISerializer;

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

    /**
     * Converts the object to String.
     * 
     * @param delimiter separator character among the fields
     * @param metadataPartSerializer to convert the {@link RingMetadataPart} into a String
     *        representation
     */
    public String toStringWithDelimiter(String delimiter,
            ISerializer<String, RingMetadataPart> metadataPartSerializer) {
        StringBuilder sb = new StringBuilder();
        for (RingMetadataPart metadataPart : metadataParts) {
            sb.append(metadataPartSerializer.serialize(metadataPart));
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        ISerializer<String, RingMetadataPart> defaultSerializer =
                new ISerializer<String, RingMetadataPart>() {
                    @Override
                    public String serialize(RingMetadataPart target) {
                        return target.toString();
                    }
                };

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String delimiter = ", ";
        sb.append(toStringWithDelimiter(delimiter, defaultSerializer));
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
