package weloveclouds.commons.utils.ecryption;

import weloveclouds.commons.utils.ecryption.exception.DecryptionException;
import weloveclouds.commons.utils.ecryption.exception.EncryptionException;

/**
 * Common methods to encrypt and decrypt a T-typed object.
 * 
 * @author Benedek
 */
public interface IEncryptionUtil<T> {

    public T encrypt(T decrypted) throws EncryptionException;

    public T decrypt(T encrypted) throws DecryptionException;
}
