package weloveclouds.commons.communication;

import weloveclouds.commons.communication.resend.strategy.ExponentialBackoffResendStrategy;
import weloveclouds.commons.communication.resend.strategy.ExponentialBackoffResendStrategyFactory;
import weloveclouds.commons.communication.resend.strategy.ExponentialBackoffResendWithResponseStrategy;
import weloveclouds.commons.communication.resend.strategy.IPacketResendStrategy;
import weloveclouds.communication.api.ICommunicationApi;

/**
 * A factory to create {@link NetworkPacketResender} instances which overcome network errors
 * 
 * @author Benedek
 */
public class NetworkPacketResenderFactory {

    private ExponentialBackoffResendStrategyFactory exponentialResendStrategyFactory;

    public NetworkPacketResenderFactory() {
        this.exponentialResendStrategyFactory = new ExponentialBackoffResendStrategyFactory();
    }

    /**
     * Creates a {@link NetworkPacketResender} based on the strategy it uses.
     * 
     * @param backoffStrategy that shall be used for resend
     */
    public NetworkPacketResender createResender(IPacketResendStrategy resendStrategy) {
        return new NetworkPacketResender(resendStrategy);
    }

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffResendStrategy}
     * based on the following parameterization.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     */
    public NetworkPacketResender createResenderWithExponentialBackoff(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet) {
        return new NetworkPacketResender(
                exponentialResendStrategyFactory.createExponentialBackoffResendStrategy(
                        maxNumberOfAttempts, communicationApi, packet));
    }

    /**
     * Creates a {@link NetworkPacketResender} which uses
     * {@link ExponentialBackoffResendWithResponseStrategy} based on the following parameterization.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     */
    public NetworkPacketResender createResenderWithResponseWithExponentialBackoff(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet) {
        return new NetworkPacketResender(
                exponentialResendStrategyFactory.createExponentialBackoffResendWithResponseStrategy(
                        maxNumberOfAttempts, communicationApi, packet));
    }

}
