package weloveclouds.commons.hashing.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;

/**
 * Utility class for the {@link Hash}.
 *
 * @author Benedek
 */
public class HashingUtils {

    /**
     * Divides the referred hashRange into {@link numberOfSubRanges} number of sub ranges which have
     * equal size.
     */
    public static List<HashRange> divideHashRangeIntoEqualSubranges(HashRange hashRange,
            int numberOfSubRanges) {
        List<HashRange> subRanges = new ArrayList<>();
        BigInteger rangeIntensity = hashRange.getEndValue().subtract(hashRange.getStartValue());
        BigInteger subRangeIntensity = rangeIntensity.divide(BigInteger.valueOf(numberOfSubRanges));

        for (BigInteger i = hashRange.getStartValue(); isSmallerThan(i, hashRange.getEndValue()); i
                .add(subRangeIntensity)) {
            byte[] subRangeStart = i.toByteArray();
            byte[] subRangeEnd =
                    i.add(subRangeIntensity.subtract(BigInteger.valueOf(1))).toByteArray();
            subRanges.add(new HashRange.Builder().begin(new Hash(subRangeStart))
                    .end(new Hash(subRangeEnd)).build());
        }
        return subRanges;
    }

    /**
     * @return MD5 hash representation of the parameter String or null if the algorithm is not
     *         available
     */
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
