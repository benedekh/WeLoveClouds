package weloveclouds.commons.communication;

import java.io.IOException;

import org.apache.log4j.Logger;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.tasks.Status;

public class NetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResender.class);

    private int attemptNumber;
    private IPacketResendStrategy resendStrategy;

    public NetworkPacketResender(int attemptNumber, IPacketResendStrategy resendStrategy) {
        this.attemptNumber = attemptNumber;
        this.resendStrategy = resendStrategy;
    }

    public byte[] resendPacket(ICommunicationApi communicationApi, byte[] packet)
            throws IOException {
        resendStrategy.configure(attemptNumber, communicationApi, packet);

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
                    resendStrategy.incrementNumberOfAttempts();
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
