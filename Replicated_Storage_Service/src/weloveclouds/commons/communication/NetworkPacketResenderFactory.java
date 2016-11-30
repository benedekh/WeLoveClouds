package weloveclouds.commons.communication;

import weloveclouds.commons.communication.resend.strategy.ExponentialBackoffResendStrategy;
import weloveclouds.commons.communication.resend.strategy.ExponentialBackoffResendStrategyFactory;
import weloveclouds.communication.api.ICommunicationApi;


public class NetworkPacketResenderFactory {

    private ExponentialBackoffResendStrategyFactory exponentialResendStrategyFactory;

    public NetworkPacketResenderFactory() {
        this.exponentialResendStrategyFactory = new ExponentialBackoffResendStrategyFactory();
    }

    /**
     * Creates a {@link NetworkPacketResender} which uses {@link ExponentialBackoffResendStrategy}.
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

    public NetworkPacketResenderWithResponse createResenderWithResponseWithExponentialBackoff(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet) {
        return new NetworkPacketResenderWithResponse(
                exponentialResendStrategyFactory.createExponentialBackoffResendWithResponseStrategy(
                        maxNumberOfAttempts, communicationApi, packet));
    }

}
