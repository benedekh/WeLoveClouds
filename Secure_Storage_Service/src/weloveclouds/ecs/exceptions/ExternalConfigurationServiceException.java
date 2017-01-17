package weloveclouds.ecs.exceptions;

import weloveclouds.commons.exceptions.ClientSideException;

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
