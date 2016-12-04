package weloveclouds.commons.communication;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.resend.strategy.IPacketResendStrategy;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A helper class which uses different strategies for sending a packet over the network.
 * 
 * @author Benedek
 */
public class NetworkPacketResender {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResender.class);

    private IPacketResendStrategy resendStrategy;

    /**
     * @param resendStrategy the strategy to be used for resending the packet
     */
    public NetworkPacketResender(IPacketResendStrategy resendStrategy) {
        this.resendStrategy = resendStrategy;
    }

    public void resendPacket() throws IOException {
        while (resendStrategy.getExecutionStatus() == Status.RUNNING) {
            resendStrategy.tryAgain();

            if (resendStrategy.getExecutionStatus() == Status.RUNNING) {
                LOGGER.info(
                        "Last try was unsuccessful, so incrementing the number of tries by one.");
                resendStrategy.incrementNumberOfAttemptsByOne();
            }
        }

        switch (resendStrategy.getExecutionStatus()) {
            case COMPLETED:
                LOGGER.info("Packet was sucessfully sent.");
                return;
            case FAILED:
                LOGGER.info("Retry resend failed due to an exception.");
                throw resendStrategy.getException();
            default:
                String errorMessage =
                        join("", "Illegal state (", resendStrategy.getExecutionStatus().toString(),
                                ") for the resend startegy.");
                LOGGER.error(errorMessage);
                throw new IOException(errorMessage);
        }
    }


}
