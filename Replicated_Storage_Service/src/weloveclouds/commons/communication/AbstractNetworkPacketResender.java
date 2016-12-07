package weloveclouds.commons.communication;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.IBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;

/**
 * Common fields and methods for a NetworkPacketResender.
 *
 * @author Benedek
 */
public abstract class AbstractNetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(AbstractNetworkPacketResender.class);

    protected int maxNumberOfAttempts;
    protected ICommunicationApi communicationApi;
    protected byte[] packetToBeSent;

    protected int numberOfAttemptsSoFar;
    protected IBackoffIntervalComputer backoffIntervalComputer;

    /**
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     * @param waitStrategy strategy that determines the length of waiting before the next attempt
     * 
     * @throws IllegalArgumentException if maxNumberOfAttempts < 0
     */
    public AbstractNetworkPacketResender(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet,
            IBackoffIntervalComputer waitStrategy) {
        if (maxNumberOfAttempts < 0) {
            throw new IllegalArgumentException("Number of attempts has to be positive.");
        }

        this.maxNumberOfAttempts = maxNumberOfAttempts;
        this.communicationApi = communicationApi;
        this.packetToBeSent = packet;
        this.backoffIntervalComputer = waitStrategy;
    }

    /**
     * Sleeps based on which attempts it was recently in.
     * 
     * @throws InterruptedException if the thread was waken up from sleep
     */
    protected void sleepBeforeNextAttempt() throws InterruptedException {
        LOGGER.info(join("", "#",
                String.valueOf(numberOfAttemptsSoFar) + " resend attempts were made out of #",
                String.valueOf(maxNumberOfAttempts), " attempts."));
        Thread.sleep(
                backoffIntervalComputer.computeIntervalFrom(numberOfAttemptsSoFar).getMillis());
    }

    /**
     * Resends the packet over and over again, until it is successfully submitted or it cannot be
     * sent over the network after several attempts.
     * 
     * @return the response byte[] if there is any. Otherwise an empty byte[] is returned.
     * @throws IOException if an error occurs
     */
    public abstract byte[] resendPacket() throws IOException;

}
