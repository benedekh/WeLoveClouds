package weloveclouds.hashing.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import weloveclouds.hashing.models.Hash;

/**
 * Utility class for the {@link Hash}.
 * 
 * @author Benedek
 */
public class HashingUtil {

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
}
