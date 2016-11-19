package weloveclouds.server.services.exceptions;

public class ServiceIsInitializedException extends Exception {

    private static final long serialVersionUID = 4543588054771498859L;

    public ServiceIsInitializedException() {
        super("Data access service is already initialized.");
    }

}
