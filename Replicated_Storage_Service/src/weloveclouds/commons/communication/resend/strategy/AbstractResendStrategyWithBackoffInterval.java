package weloveclouds.commons.communication.resend.strategy;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.IBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.tasks.Status;

/**
 * Common fields and methods for a BackoffResendStrategy that has a {@link IBackoffIntervalComputer}
 * to calculate the waiting intervals between resends.
 * 
 * @author Benedek
 */
public abstract class AbstractResendStrategyWithBackoffInterval implements IPacketResendStrategy {

    private static final Logger LOGGER =
            Logger.getLogger(AbstractResendStrategyWithBackoffInterval.class);

    protected int maxNumberOfAttempts;
    protected ICommunicationApi communicationApi;
    protected byte[] packetToBeSent;

    protected int numberOfAttemptsSoFar;
    protected IBackoffIntervalComputer backoffIntervalComputer;

    protected IOException exception;
    protected Status executionStatus;

    /**
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     * @param backoffIntervalComputer an interval computer that determines the length of waiting
     *        before the next attempt
     */
    public AbstractResendStrategyWithBackoffInterval(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet,
            IBackoffIntervalComputer backoffIntervalComputer) {
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

    /**
     * Sleeps based on which attempts it was recently in.
     * 
     * @throws InterruptedException if the thread was waken up from sleep
     */
    protected void sleepBeforeNextAttempt() throws InterruptedException {
        if (executionStatus != Status.COMPLETED) {
            LOGGER.info(join("", "#",
                    String.valueOf(numberOfAttemptsSoFar) + " resend attempts were made out of #",
                    String.valueOf(maxNumberOfAttempts), " attempts."));
            Thread.sleep(
                    backoffIntervalComputer.computeIntervalFrom(numberOfAttemptsSoFar).getMillis());
        }
    }

}
