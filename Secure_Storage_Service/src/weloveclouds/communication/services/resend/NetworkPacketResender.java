package weloveclouds.communication.services.resend;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.retryer.IBackoffIntervalComputer;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.ICommunicationService;
import weloveclouds.communication.services.IConcurrentCommunicationService;

/**
 * A helper class which can use different wait strategies between resends for sending a packet over
 * the network.
 * 
 * @author Benedek
 */
public class NetworkPacketResender extends AbstractNetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResender.class);

    public NetworkPacketResender(int maxNumberOfAttempts, byte[] packet,
            IBackoffIntervalComputer waitStrategy) {
        super(maxNumberOfAttempts, packet, waitStrategy);
    }

    @Override
    public byte[] sendWith(ICommunicationService communicationService) throws IOException {
        while (numberOfAttemptsSoFar < maxNumberOfAttempts) {
            try {
                try {
                    if (!communicationService.isConnected()) {
                        String errorMessage = "Connection is closed, resend stopped.";
                        LOGGER.error(errorMessage);
                        throw new IOException(errorMessage);
                    }
                    LOGGER.info("Sending packet over the network.");
                    communicationService.send(packetToBeSent);
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

    @Override
    public byte[] sendWith(IConcurrentCommunicationService concurrentCommunicationService,
            Connection<?> connection) throws IOException {
        while (numberOfAttemptsSoFar < maxNumberOfAttempts) {
            try {
                try {
                    if (!connection.isConnected()) {
                        String errorMessage = "Connection is closed, resend stopped.";
                        LOGGER.error(errorMessage);
                        throw new IOException(errorMessage);
                    }
                    LOGGER.info("Sending packet over the network.");
                    concurrentCommunicationService.send(packetToBeSent, connection);
                    return new byte[0];
                } catch (IOException ex) {
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
