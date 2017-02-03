package weloveclouds.commons.utils.encryption.exception;

/**
 * Exception occurred during encryption.
 * 
 * @author Benedek
 */
public class EncryptionException extends Exception {

    private static final long serialVersionUID = 6916228715844567403L;

    public EncryptionException() {
        super("Exception occurred during encryption.");
    }

}
