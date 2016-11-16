package weloveclouds.hashing.models;

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

    private byte[] hash;

    public Hash(byte[] hash) {
        this.hash = hash;
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

}
