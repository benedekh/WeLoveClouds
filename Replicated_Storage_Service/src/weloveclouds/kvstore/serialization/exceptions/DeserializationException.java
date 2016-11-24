package weloveclouds.kvstore.serialization.exceptions;

/**
 * An exception which occurred during deserialization.
 * 
 * @author Benedek
 */
public class DeserializationException extends Exception {

    private static final long serialVersionUID = -6185550235722659201L;

    public DeserializationException(String message) {
        super(message);
    }

}
