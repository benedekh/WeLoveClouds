package weloveclouds.ecs.exceptions;

/**
 * Created by Benoit on 2016-11-22.
 */
public class ExternalConfigurationServiceException extends ClientSideException {
    public ExternalConfigurationServiceException(String message) {
        super(message);
    }

    public ExternalConfigurationServiceException(String message, Exception cause) {
        super(message, cause);
    }
}
