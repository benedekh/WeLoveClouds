package weloveclouds.server.services.datastore.exceptions;

/**
 * The data access service was already initialized.
 * 
 * @author Benedek
 */
public class ServiceIsAlreadyInitializedException extends Exception {

    private static final long serialVersionUID = 4543588054771498859L;

    public ServiceIsAlreadyInitializedException() {
        super("Data access service is already initialized.");
    }

}
