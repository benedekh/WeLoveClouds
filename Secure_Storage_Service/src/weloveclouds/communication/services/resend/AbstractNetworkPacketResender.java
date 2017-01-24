package weloveclouds.communication.services.resend;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.retryer.IBackoffIntervalComputer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.ICommunicationService;
import weloveclouds.communication.services.IConcurrentCommunicationService;

/**
 * Common fields and methods for a NetworkPacketResender.
 *
 * @author Benedek
 */
public abstract class AbstractNetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(AbstractNetworkPacketResender.class);

    protected int maxNumberOfAttempts;
    protected byte[] packetToBeSent;

    protected int numberOfAttemptsSoFar;
    protected IBackoffIntervalComputer backoffIntervalComputer;

    /**
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param packet that has to be sent over the network
     * @param waitStrategy strategy that determines the length of waiting before the next attempt
     * 
     * @throws IllegalArgumentException if maxNumberOfAttempts < 0
     */
    public AbstractNetworkPacketResender(int maxNumberOfAttempts, byte[] packet,
            IBackoffIntervalComputer waitStrategy) {
        if (maxNumberOfAttempts < 0) {
            throw new IllegalArgumentException("Number of attempts has to be positive.");
        }

        this.maxNumberOfAttempts = maxNumberOfAttempts;
        this.packetToBeSent = packet;
        this.backoffIntervalComputer = waitStrategy;
    }

    /**
     * Sleeps based on which attempts it was recently in.
     * 
     * @throws InterruptedException if the thread was waken up from sleep
     */
    protected void sleepBeforeNextAttempt() throws InterruptedException {
        LOGGER.info(StringUtils.join("", "#", numberOfAttemptsSoFar,
                " resend attempts were made out of #", maxNumberOfAttempts, " attempts."));
        Thread.sleep(
                backoffIntervalComputer.computeIntervalFrom(numberOfAttemptsSoFar).getMillis());
    }

    /**
     * Sends the packet through the referred transport service.
     * 
     * @throws IOException if any error occurs
     */
    public abstract byte[] sendWith(ICommunicationService communicationService) throws IOException;

    /**
     * Sends the packet through the referred transport service by the referred connection.
     * 
     * @throws IOException if any error occurs
     */
    public abstract byte[] sendWith(IConcurrentCommunicationService concurrentCommunicationService,
            Connection<?> connection) throws IOException;

}
