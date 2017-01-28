package weloveclouds.commons.utils.encryption;

import weloveclouds.commons.utils.encryption.exception.DecryptionException;
import weloveclouds.commons.utils.encryption.exception.EncryptionException;

/**
 * Common methods to encrypt and decrypt a T-typed object.
 * 
 * @author Benedek
 */
public interface IEncryptionUtil<T> {

    public T encrypt(T decrypted) throws EncryptionException;

    public T decrypt(T encrypted) throws DecryptionException;
}
