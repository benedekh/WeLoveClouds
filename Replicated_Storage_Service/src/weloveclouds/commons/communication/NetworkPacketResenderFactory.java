package weloveclouds.commons.communication;

/**
 * A factory to create {@link NetworkPacketResender} instances.
 * 
 * @author Benedek
 */
public class NetworkPacketResenderFactory {

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffResendStrategy}.
     * 
     * @param attemptNumber the max number of attempts for sending the packet
     */
    public NetworkPacketResender createResenderWithExponentialBackoff(int attemptNumber) {
        return new NetworkPacketResender(attemptNumber, new ExponentialBackoffResendStrategy());
    }

}
