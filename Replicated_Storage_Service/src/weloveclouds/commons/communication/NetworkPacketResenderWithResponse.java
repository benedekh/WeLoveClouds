package weloveclouds.commons.communication;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.resend.strategy.IPacketResendWithResponseStrategy;
import weloveclouds.ecs.models.tasks.Status;

public class NetworkPacketResenderWithResponse {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResenderWithResponse.class);

    private IPacketResendWithResponseStrategy resendStrategy;

    public NetworkPacketResenderWithResponse(
            IPacketResendWithResponseStrategy resendWithResponseStrategy) {
        this.resendStrategy = resendWithResponseStrategy;
    }

    /**
     * Resends the packet over the network until either a response is received for that, or the max
     * number of attempts were exceeded, or an exception occurs.
     * 
     * @return the response that was received for the packet
     * @throws IOException if any error occurs, including the exceeded number of retries
     */
    public byte[] resendPacketWithResponse() throws IOException {
        Status status = resendStrategy.getExecutionStatus();
        while (status != Status.COMPLETED) {
            resendStrategy.tryAgain();

            switch (resendStrategy.getExecutionStatus()) {
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

        switch (resendStrategy.getExecutionStatus()) {
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
