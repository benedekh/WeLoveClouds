package weloveclouds.hashing.models;

import java.math.BigInteger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.serialization.helper.ISerializer;

/**
 * The following properties should be held for the range:<br>
 * (1) end >= start, and <br>
 * (2) end <= {@link Hash#MAX_VALUE} && start >= {@link Hash#MIN_VALUE}, UNLESS <br>
 * (3) start >= end, because they wrap over. But in this case (2) should hold with slight changes:
 * start <= {@link Hash#MAX_VALUE} && end >= {@link Hash#MIN_VALUE}.
 *
 * The ends of the range are inclusive.
 *
 * @author Benedek
 */
public class HashRange {

    private Hash begin;
    private Hash end;

    protected HashRange(Builder builder) {
        this.begin = builder.begin;
        this.end = builder.end;
    }

    public Hash getStart() {
        return begin;
    }

    public Hash getEnd() {
        return end;
    }

    public BigInteger getStartValue() {
        return new BigInteger(begin.getBytes());
    }

    public BigInteger getEndValue() {
        return new BigInteger(end.getBytes());
    }

    public boolean contains(Hash target) {
        if (end.compareTo(begin) >= 0) {
            // if the range does not wrap over
            return (target.compareTo(begin) >= 0) && (target.compareTo(end) <= 0);
        } else {
            // if the range wraps over
            if (target.compareTo(Hash.MAX_VALUE) <= 0 && target.compareTo(begin) >= 0) {
                return true;
            } else if (target.compareTo(Hash.MIN_VALUE) >= 0 && target.compareTo(end) <= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Converts the object to String.
     *
     * @param betweenHashes  separator character between the {@link #start} and the {@link #end}
     *                       hash values
     * @param hashSerializer to convert the {@link Hash} into a String representation
     */
    public String toStringWithDelimiter(String betweenHashes, ISerializer<String, Hash> hashSerializer) {
        return CustomStringJoiner.join(betweenHashes, hashSerializer.serialize(begin),
                hashSerializer.serialize(end));
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "(", begin.toString(), ",", end.toString(), ")");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((begin == null) ? 0 : begin.hashCode());
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
        if (!(obj instanceof HashRange)) {
            return false;
        }
        HashRange other = (HashRange) obj;
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }
        if (begin == null) {
            if (other.begin != null) {
                return false;
            }
        } else if (!begin.equals(other.begin)) {
            return false;
        }
        return true;
    }

    /**
     * Builder pattern for creating a {@link HashRange} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private Hash begin;
        private Hash end;

        public Builder begin(Hash begin) {
            this.begin = begin;
            return this;
        }

        public Builder end(Hash end) {
            this.end = end;
            return this;
        }

        public HashRange build() {
            return new HashRange(this);
        }
    }

}
