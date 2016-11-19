package weloveclouds.hashing.models;

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

    private Hash start;
    private Hash end;

    protected HashRange(Builder builder) {
        this.start = builder.start;
        this.end = builder.end;
    }

    public boolean contains(Hash target) {
        if (end.compareTo(start) > 0) {
            // if the range does not wrap over
            return (target.compareTo(start) >= 0) && (target.compareTo(end) <= 0);
        } else {
            // if the range wraps over
            if (target.compareTo(Hash.MAX_VALUE) <= 0 && target.compareTo(start) >= 0) {
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
     * @param betweenHashes separator character between the {@link #start} and the {@link #end} hash
     *        values
     * @param hashSerializer to convert the {@link Hash} into a String representation
     */
    public String toStringWithDelimiter(String betweenHashes,
            ISerializer<String, Hash> hashSerializer) {
        return CustomStringJoiner.join(betweenHashes, hashSerializer.serialize(start),
                hashSerializer.serialize(end));
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "(", start.toString(), ",", end.toString(), ")");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
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
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
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
        private Hash start;
        private Hash end;

        public Builder start(Hash start) {
            this.start = start;
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
