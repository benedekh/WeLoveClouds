package weloveclouds.commons.utils.encryption.exception;

/**
 * Exception occurred during decryption.
 * 
 * @author Benedek
 */
public class DecryptionException extends Exception {

    private static final long serialVersionUID = 3375652740475736607L;

    public DecryptionException() {
        super("Exception occurred during decryption.");
    }

}
