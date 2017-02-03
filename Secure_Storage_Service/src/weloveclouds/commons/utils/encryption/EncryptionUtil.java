package weloveclouds.commons.utils.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.encryption.exception.DecryptionException;
import weloveclouds.commons.utils.encryption.exception.EncryptionException;

/**
 * Utility class to encrypt or decrypt an object.
 * 
 * @author Benedek
 *
 * @param <T> type of the object that will be encrypted or decrypted
 */
public abstract class EncryptionUtil<T> implements IEncryptionUtil<T> {

    private static final Logger LOGGER = Logger.getLogger(EncryptionUtil.class);

    private static final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private static final int KEY_LENGTH = 128;

    private SecretKey key;
    private byte[] initializationVector;

    public EncryptionUtil(SecretKey key, byte[] initializationVector) {
        this.key = key;
        this.initializationVector = initializationVector;
    }

    public EncryptionUtil() {
        this.key = generateKey();
        this.initializationVector = generateIV();
    }

    protected byte[] encrypt(byte[] decrypted) throws EncryptionException {
        try {
            if (decrypted == null) {
                return null;
            } else {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                IvParameterSpec ips = new IvParameterSpec(initializationVector);
                cipher.init(Cipher.ENCRYPT_MODE, key, ips);
                return cipher.doFinal(decrypted);
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new EncryptionException();
        }
    }

    protected byte[] decrypt(byte[] encrypted) throws DecryptionException {
        try {
            if (encrypted == null) {
                return null;
            } else {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                IvParameterSpec ips = new IvParameterSpec(initializationVector);
                cipher.init(Cipher.DECRYPT_MODE, key, ips);
                return cipher.doFinal(encrypted);
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new DecryptionException();
        }
    }

    private SecretKey generateKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(KEY_LENGTH);
            return kg.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    private byte[] generateIV() {
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] initializationVector = new byte[16]; // IV for AES is always 16 bytes
        randomSecureRandom.nextBytes(initializationVector);
        return initializationVector;
    }

}
