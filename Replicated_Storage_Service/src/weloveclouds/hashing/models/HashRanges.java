package weloveclouds.hashing.models;

import java.util.Collections;
import java.util.Set;

/**
 * Encapsulates @{HashRange} instances.
 * 
 * @author Benedek
 */
public class HashRanges {

    private Set<HashRange> hashRanges;

    public HashRanges(Set<HashRange> hashRanges) {
        this.hashRanges = hashRanges;
    }

    public Set<HashRange> getHashRanges() {
        return Collections.unmodifiableSet(hashRanges);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashRanges == null) ? 0 : hashRanges.hashCode());
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
        if (!(obj instanceof HashRanges)) {
            return false;
        }
        HashRanges other = (HashRanges) obj;
        if (hashRanges == null) {
            if (other.hashRanges != null) {
                return false;
            }
        } else if (!hashRanges.equals(other.hashRanges)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String delimiter = ", ";

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (HashRange range : hashRanges) {
            sb.append(range);
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        sb.append("}");

        return sb.toString();
    }



}
