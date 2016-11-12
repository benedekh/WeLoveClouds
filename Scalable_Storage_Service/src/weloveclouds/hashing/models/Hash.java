package weloveclouds.hashing.models;

import weloveclouds.client.utils.CustomStringJoiner;

public class Hash implements Comparable<Hash> {

    private byte[] hash;

    public Hash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] hash() {
        return hash;
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
            int thisOctet = Math.abs(hash[i]); // to disregard the sign bit
            int otherOctet = Math.abs(other.hash[i]); // to disregard the sign bit

            if (thisOctet == otherOctet) {
                continue;
            } else if (thisOctet < otherOctet) {
                return LESS;
            } else if (thisOctet > otherOctet) {
                return GREATER;
            }
        }

        return EQUALS;
    }

}
