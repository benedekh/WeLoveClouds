package weloveclouds.communication.services.resend;

import org.joda.time.Duration;

import weloveclouds.commons.retryer.ExponentialBackoffIntervalComputer;

/**
 * A factory to create {@link NetworkPacketResender} instances which overcome network errors
 * 
 * @author Benedek
 */
public class NetworkPacketResenderFactory {

    private static final Duration MINIMAL_INTERVAL = new Duration(300);

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffIntervalComputer}
     * based on the following parameterization. Default {@link #MINIMAL_INTERVAL} duration is used
     * between two resends.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param packet that has to be sent over the network
     */
    public AbstractNetworkPacketResender createResenderWithExponentialBackoff(
            int maxNumberOfAttempts, byte[] packet) {
        return new NetworkPacketResender(maxNumberOfAttempts, packet,
                new ExponentialBackoffIntervalComputer(MINIMAL_INTERVAL));
    }

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffIntervalComputer}
     * based on the following parameterization.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param packet that has to be sent over the network
     * @param minimalInterval how much time shall elapse between two resend attempts
     */
    public AbstractNetworkPacketResender createResenderWithExponentialBackoff(
            int maxNumberOfAttempts, byte[] packet, Duration minimalInterval) {
        return new NetworkPacketResender(maxNumberOfAttempts, packet,
                new ExponentialBackoffIntervalComputer(minimalInterval));
    }

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffIntervalComputer}
     * based on the following parameterization. Default {@link #MINIMAL_INTERVAL} duration is used
     * between two resends.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param packet that has to be sent over the network
     */
    public AbstractNetworkPacketResender createResenderWithResponseWithExponentialBackoff(
            int maxNumberOfAttempts, byte[] packet) {
        return new NetworkPacketResenderWithResponse(maxNumberOfAttempts, packet,
                new ExponentialBackoffIntervalComputer(MINIMAL_INTERVAL));
    }

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffIntervalComputer}
     * based on the following parameterization.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param packet that has to be sent over the network
     * @param minimalInterval how much time shall elapse between two resend attempts
     */
    public AbstractNetworkPacketResender createResenderWithResponseWithExponentialBackoff(
            int maxNumberOfAttempts, byte[] packet, Duration minimalInterval) {
        return new NetworkPacketResenderWithResponse(maxNumberOfAttempts, packet,
                new ExponentialBackoffIntervalComputer(minimalInterval));
    }

}
