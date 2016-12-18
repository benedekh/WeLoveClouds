package weloveclouds.commons.hashing.models;

import java.util.Arrays;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents an arbitrary long hash value stored as a byte array.
 *
 * @author Benedek
 */
public class Hash implements Comparable<Hash> {

    public static final Hash MAX_VALUE;
    public static final Hash MIN_VALUE;

    static {
        int numberOfBytes = 16;
        byte[] maxValues = new byte[numberOfBytes];
        byte[] minValues = new byte[numberOfBytes];

        for (int i = 0; i < numberOfBytes; ++i) {
            maxValues[i] = Byte.MAX_VALUE;
            minValues[i] = Byte.MIN_VALUE;
        }

        MAX_VALUE = new Hash(maxValues);
        MIN_VALUE = new Hash(minValues);
    }

    private final byte[] hash;

    public Hash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getBytes() {
        return this.hash;
    }

    /**
     * Creates a new Hash, whose value is incremented by one.
     */
    public Hash incrementByOne() {
        byte[] hashArray = Arrays.copyOf(hash, hash.length);

        for (int i = hashArray.length - 1; i >= 0; --i) {
            byte previousValue = hashArray[i];
            hashArray[i] = (byte) (previousValue + 1);

            boolean overflowHappened =
                    hashArray[i] == Byte.MIN_VALUE && previousValue == Byte.MAX_VALUE;
            if (!overflowHappened) {
                break;
            }
        }

        return new Hash(hashArray);
    }

    /**
     * Creates a new Hash, whose value is decremented by one.
     */
    public Hash decrementByOne() {
        byte[] hashArray = Arrays.copyOf(hash, hash.length);

        for (int i = hashArray.length - 1; i >= 0; --i) {
            byte previousValue = hashArray[i];
            hashArray[i] = (byte) (previousValue - 1);

            boolean underflowHappened =
                    hashArray[i] == Byte.MAX_VALUE && previousValue == Byte.MIN_VALUE;
            if (!underflowHappened) {
                break;
            }
        }

        return new Hash(hashArray);
    }

    @Override
    public int compareTo(Hash other) {
        final int LESS = -1;
        final int EQUALS = 0;
        final int GREATER = 1;

        if (hash.length != other.hash.length) {
            throw new IllegalArgumentException(
                    StringUtils.join(" ", "Two objects need to have the same length. This:",
                            hash.length, "Other:", other.hash.length));
        }

        for (int i = 0; i < hash.length; ++i) {
            int compareResult = Byte.compare(hash[i], other.hash[i]);

            if (compareResult == 0) {
                continue;
            } else if (compareResult < 0) {
                return LESS;
            } else if (compareResult > 0) {
                return GREATER;
            }
        }

        return EQUALS;
    }

    /**
     * Converts the object to String, using the delimiter character as a separator among the byte
     * values.
     */
    public String toStringWithDelimiter(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.valueOf(b));
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(toStringWithDelimiter("|"));
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(hash);
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
        if (!(obj instanceof Hash)) {
            return false;
        }
        Hash other = (Hash) obj;
        if (!Arrays.equals(hash, other.hash)) {
            return false;
        }
        return true;
    }

}
