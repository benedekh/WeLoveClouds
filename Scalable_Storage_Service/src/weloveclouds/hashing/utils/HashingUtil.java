package weloveclouds.hashing.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import weloveclouds.hashing.models.Hash;

public class HashingUtil {

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
