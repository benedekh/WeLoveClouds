package weloveclouds.commons.utils.ecryption;

import java.nio.charset.Charset;

import javax.crypto.SecretKey;

import com.google.common.base.Charsets;

import weloveclouds.commons.utils.ecryption.exception.DecryptionException;
import weloveclouds.commons.utils.ecryption.exception.EncryptionException;

/**
 * Utility class which encrypts a String.
 * 
 * @author Benedek
 */
public class StringEncryptionUtil extends EncryptionUtil<String> {

    private static final Charset CHARSET = Charsets.UTF_8;

    public StringEncryptionUtil() {
        super();
    }

    public StringEncryptionUtil(SecretKey key, byte[] initializationVector) {
        super(key, initializationVector);
    }

    @Override
    public String encrypt(String decrypted) throws EncryptionException {
        if (decrypted == null) {
            return null;
        }
        byte[] encrypted = encrypt(decrypted.getBytes(CHARSET));
        return new String(encrypted, CHARSET);
    }

    @Override
    public String decrypt(String encrypted) throws DecryptionException {
        if (encrypted == null) {
            return null;
        }
        byte[] decrypted = decrypt(encrypted.getBytes(CHARSET));
        return new String(decrypted, CHARSET);
    }



}
