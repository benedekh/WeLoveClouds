package weloveclouds.commons.utils.encryption;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import weloveclouds.commons.utils.encryption.exception.DecryptionException;
import weloveclouds.commons.utils.encryption.exception.EncryptionException;

/**
 * Utility class which encrypts a String.
 * 
 * @author Benedek
 */
public class StringEncryptionUtil extends EncryptionUtil<String> {

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
        byte[] encrypted = encrypt(decrypted.getBytes());
        return DatatypeConverter.printBase64Binary(encrypted);
    }

    @Override
    public String decrypt(String encrypted) throws DecryptionException {
        if (encrypted == null) {
            return null;
        }
        byte[] decrypted = decrypt(DatatypeConverter.parseBase64Binary(encrypted));
        return new String(decrypted);
    }



}
