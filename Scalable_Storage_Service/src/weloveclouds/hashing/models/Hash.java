package weloveclouds.hashing.models;

import java.util.Arrays;

import weloveclouds.client.utils.CustomStringJoiner;

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
            throw new IllegalArgumentException(CustomStringJoiner.join(" ",
                    "Two objects need to have the same length. This:", String.valueOf(hash.length),
                    "Other:", String.valueOf(other.hash.length)));
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(CustomStringJoiner.join("", String.valueOf(b), "|"));
        }
        return sb.toString();
    }

}
