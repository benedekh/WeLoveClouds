package weloveclouds.hashing.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;

public class HashingUtil {

    public static List<HashRange> divideHashRangeIntoEqualSubranges(HashRange hashRange, int
            numberOfSubRanges) {
        List<HashRange> subRanges = new ArrayList<>();
        BigInteger rangeIntensity = hashRange.getEndValue().subtract(hashRange.getStartValue());
        BigInteger subRangeIntensity = rangeIntensity.divide(BigInteger.valueOf(numberOfSubRanges));

        for (BigInteger i = hashRange.getStartValue(); isSmallerThan(i, hashRange.getEndValue()); i
                .add(subRangeIntensity)) {
            byte[] subRangeStart = i.toByteArray();
            byte[] subRangeEnd = i.add(subRangeIntensity.subtract(BigInteger.valueOf(1)))
                    .toByteArray();
            subRanges.add(new HashRange.Builder()
                    .start(new Hash(subRangeStart))
                    .end(new Hash(subRangeEnd))
                    .build());
        }
        return subRanges;
    }

    public static Hash getHash(String input) {
        Hash hash = null;

        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digested = instance.digest(input.getBytes());
            hash = new Hash(digested);
        } catch (NoSuchAlgorithmException e) {
            // MD5 always exist
        }

        return hash;
    }

    private static boolean isSmallerThan(BigInteger value1, BigInteger value2) {
        return value1.compareTo(value2) == -1;
    }
}
