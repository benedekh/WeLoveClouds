package weloveclouds.ecs.exceptions;

/**
 * Created by Benoit on 2016-11-21.
 */
public class InvalidConfigurationException extends ClientSideException {
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Exception cause) {
        super(message, cause);
    }
}
