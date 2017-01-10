package weloveclouds.ecs.exceptions.task;

/**
 * Created by Benoit on 2016-11-19.
 */
public class RetryableException extends RuntimeException {
    public RetryableException(String message, Exception cause) {
        super(message, cause);
    }
}
