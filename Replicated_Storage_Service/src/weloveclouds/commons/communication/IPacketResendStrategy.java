package weloveclouds.commons.communication;

import java.io.IOException;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.tasks.Status;

/**
 * Common methods for packet resend strategies.
 * 
 * @author Benedek
 */
public interface IPacketResendStrategy {

    /**
     * Initialize the strategy with the following parameters.
     * 
     * @param attemptNumber maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network.
     */
    void initialize(int attemptNumber, ICommunicationApi communicationApi, byte[] packet);

    /**
     * @return the execution status of the strategy
     */
    Status getExecutionStatus();

    /**
     * @return any exception that was thrown during the execution of the startegy
     */
    IOException getException();

    /**
     * @return the response packet that arrived after sending the packet that had to be sent over
     *         the network
     */
    byte[] getResponse();

    /**
     * Start a new round for the resending.
     */
    void tryAgain();

    /**
     * Increments the number of attempts were made so far by one.
     */
    void incrementNumberOfAttemptsByOne();
}
