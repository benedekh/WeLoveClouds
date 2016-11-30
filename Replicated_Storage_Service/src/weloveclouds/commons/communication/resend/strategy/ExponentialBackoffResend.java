package weloveclouds.commons.communication.resend.strategy;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.ExponentialBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A strategy class which implements the 'well-known' exponential backoff strategy used in TCP, for
 * resending packet over the network a couple of times after each other.
 * 
 * @author Benedek
 */
public class ExponentialBackoffResend implements IPacketResendStrategy {

    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffResend.class);

    protected int maxNumberOfAttempts;
    protected ICommunicationApi communicationApi;
    protected byte[] packetToBeSent;

    protected int numberOfAttemptsSoFar;
    protected ExponentialBackoffIntervalComputer backoffIntervalComputer;

    protected IOException exception;
    protected Status executionStatus;

    /**
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     */
    public ExponentialBackoffResend(int maxNumberOfAttempts, ICommunicationApi communicationApi,
            byte[] packet, ExponentialBackoffIntervalComputer backoffIntervalComputer) {
        if (maxNumberOfAttempts < 0) {
            throw new IllegalArgumentException("Number of attempts has to be positive.");
        }

        this.maxNumberOfAttempts = maxNumberOfAttempts;
        this.communicationApi = communicationApi;
        this.packetToBeSent = packet;
        this.backoffIntervalComputer = backoffIntervalComputer;

        this.executionStatus = Status.RUNNING;
    }

    @Override
    public void tryAgain() {
        try {
            if (executionStatus == Status.RUNNING && numberOfAttemptsSoFar < maxNumberOfAttempts) {
                try {
                    LOGGER.info("Sending packet over the network.");
                    communicationApi.send(packetToBeSent);
                    executionStatus = Status.COMPLETED;
                } catch (UnableToSendContentToServerException ex) {
                    LOGGER.info(join("", "#",
                            String.valueOf(numberOfAttemptsSoFar)
                                    + " resend attempts were made out of #",
                            String.valueOf(maxNumberOfAttempts), " attempts."));
                    Thread.sleep(backoffIntervalComputer.computeIntervalFrom(numberOfAttemptsSoFar)
                            .getMillis());
                }
            } else if (numberOfAttemptsSoFar >= maxNumberOfAttempts) {
                String message = "Max number of retries have been reached.";
                LOGGER.info(message);
                exception = new IOException(message);
                executionStatus = Status.FAILED;
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public Status getExecutionStatus() {
        return executionStatus;
    }

    @Override
    public IOException getException() {
        return exception;
    }

    @Override
    public void incrementNumberOfAttemptsByOne() {
        numberOfAttemptsSoFar++;
    }

}
