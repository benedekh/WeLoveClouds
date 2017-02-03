package weloveclouds.commons.hashing.models;

import java.math.BigInteger;

import weloveclouds.commons.utils.StringUtils;

/**
 * The following properties should be held for the range:<br>
 * (1) {@link #end} >= {@link #begin}, and<br>
 * (2) {@link #end} <= {@link Hash#MAX_VALUE} && {@link #begin} >= {@link Hash#MIN_VALUE},
 * UNLESS<br>
 * (3) {@link #begin} >= {@link #end}, because they wrap over. But in this case (2) should hold with
 * slight changes: {@link #begin} <= {@link Hash#MAX_VALUE} && {@link #end} >=
 * {@link Hash#MIN_VALUE}.
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

    @Override
    public String toString() {
        return StringUtils.join("", "(", getStartValue(), ",", getEndValue(), ")");
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
