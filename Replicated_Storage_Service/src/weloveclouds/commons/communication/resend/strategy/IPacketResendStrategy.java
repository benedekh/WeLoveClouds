package weloveclouds.commons.communication.resend.strategy;

import java.io.IOException;

import weloveclouds.ecs.models.tasks.Status;

/**
 * Common methods for packet resend strategies.
 * 
 * @author Benedek
 */
public interface IPacketResendStrategy {

    /**
     * @return the execution status of the strategy
     */
    Status getExecutionStatus();

    /**
     * Default return value should be an empty byte[] if we don't expect for any responses.
     * 
     * @return the response packet that arrived after sending the packet that had to be sent over
     *         the network
     */
    byte[] getResponse();

    /**
     * @return any exception that was thrown during the execution of the startegy
     */
    IOException getException();

    /**
     * Start a new round for the resending.
     */
    void tryAgain();

    /**
     * Increments the number of attempts were made so far by one.
     */
    void incrementNumberOfAttemptsByOne();
}
