package weloveclouds.commons.exceptions;

/**
 * Represents an initialization error which occured when a service or an object would be initialized.
 * 
 * @author Benedek
 */
public class InitializationError extends Error {

    private static final long serialVersionUID = -3542263296214238843L;

    public InitializationError(String message) {
        super(message);
    }


}
