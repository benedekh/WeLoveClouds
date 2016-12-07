package weloveclouds.commons.communication;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.IBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;

/**
 * A helper class which can use different wait strategies between resends for sending a packet over
 * the network.
 * 
 * @author Benedek
 */
public class NetworkPacketResender extends AbstractNetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResender.class);

    public NetworkPacketResender(int maxNumberOfAttempts, ICommunicationApi communicationApi,
            byte[] packet, IBackoffIntervalComputer waitStrategy) {
        super(maxNumberOfAttempts, communicationApi, packet, waitStrategy);
    }

    @Override
    public byte[] resendPacket() throws IOException {
        while (numberOfAttemptsSoFar < maxNumberOfAttempts) {
            try {
                try {
                    LOGGER.info("Sending packet over the network.");
                    communicationApi.send(packetToBeSent);
                    return new byte[0];
                } catch (UnableToSendContentToServerException ex) {
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                }
            } catch (InterruptedException ex) {
                LOGGER.error(ex);
                throw new IOException("Resend unexpectedly stopped.");
            }
        }

        String message = "Max number of retries have been reached.";
        LOGGER.info(message);
        throw new IOException(message);
    }

}
