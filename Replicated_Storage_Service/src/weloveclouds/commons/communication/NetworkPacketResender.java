package weloveclouds.commons.communication;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A helper class which uses different strategies for resending a packet over the network.
 * 
 * @author Benedek
 */
public class NetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResender.class);

    private int attemptNumber;
    private IPacketResendStrategy resendStrategy;

    /**
     * @param attemptNumber How many times it has to retry the send?
     * @param resendStrategy the strategy to be used for resending the packet
     */
    public NetworkPacketResender(int attemptNumber, IPacketResendStrategy resendStrategy)
            throws IllegalArgumentException {
        if (attemptNumber < 0) {
            throw new IllegalArgumentException("Number of attempts has to be positive.");
        }
        this.attemptNumber = attemptNumber;
        this.resendStrategy = resendStrategy;
    }

    /**
     * Resends the packet over the network until either a response is received for that, or the max
     * number of attempts were exceeded, or an exception occurs.
     * 
     * @param communicationApi the communication channel through the packet has to be sent
     * @param packet that has to be sent over the network
     * @return the response that was received for the packet
     * @throws IOException if any error occurs, including the exceeded number of retries
     */
    public byte[] resendPacket(ICommunicationApi communicationApi, byte[] packet)
            throws IOException {
        resendStrategy.initialize(attemptNumber, communicationApi, packet);

        Status status = resendStrategy.getExecutionStatus();
        while (status != Status.COMPLETED) {
            resendStrategy.tryAgain();

            status = resendStrategy.getExecutionStatus();
            switch (status) {
                case COMPLETED:
                    LOGGER.info("Retry resend finished, because response message was received.");
                    return resendStrategy.getResponse();
                case FAILED:
                    IOException exception = resendStrategy.getException();
                    LOGGER.info("Retry resend failed due to an exception.");
                    LOGGER.error(exception);
                    throw exception;
                case RUNNING:
                    LOGGER.info(
                            "Last try was unsuccessful, so incrementing the number of tries by one.");
                    resendStrategy.incrementNumberOfAttemptsByOne();
                    break;
                default:
                    String errorMessage = join("", "Illegal state (", status.toString(),
                            ") for the resend startegy.");
                    LOGGER.error(errorMessage);
                    throw new IOException(errorMessage);
            }
        }

        status = resendStrategy.getExecutionStatus();
        switch (status) {
            case COMPLETED:
                LOGGER.info("Retry resend finished, because response message was received.");
                return resendStrategy.getResponse();
            case FAILED:
                IOException exception = resendStrategy.getException();
                LOGGER.info("Retry resend failed due to an exception.");
                LOGGER.error(exception);
                throw exception;
            default:
                String errorMessage = join("", "Illegal state (", status.toString(),
                        ") for the resend startegy.");
                LOGGER.error(errorMessage);
                throw new IOException(errorMessage);

        }
    }
}
