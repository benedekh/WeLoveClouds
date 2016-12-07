package weloveclouds.commons.communication.resend.strategy;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.IBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A strategy class which implements the 'well-known' exponential backoff strategy used in TCP, for
 * resending packet over the network a couple of times after each other.
 * 
 * @author Benedek
 */
public class ExponentialBackoffResendStrategy extends AbstractResendStrategyWithBackoffInterval {

    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffResendStrategy.class);

    public ExponentialBackoffResendStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet,
            IBackoffIntervalComputer backoffIntervalComputer) {
        super(maxNumberOfAttempts, communicationApi, packet, backoffIntervalComputer);
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
                    sleepBeforeNextAttempt();
                }
            } else if (numberOfAttemptsSoFar >= maxNumberOfAttempts) {
                String message = "Max number of retries have been reached.";
                LOGGER.info(message);
                exception = new IOException(message);
                executionStatus = Status.FAILED;
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
            exception = new IOException("Resend unexpectedly stopped.");
            executionStatus = Status.FAILED;
        }
    }

    @Override
    public byte[] getResponse() {
        return new byte[0];
    }

}
