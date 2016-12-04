package weloveclouds.commons.communication;

import java.io.IOException;

import weloveclouds.commons.communication.resend.strategy.ExponentialBackoffResendStrategyFactory;

public abstract class AbstractNetworkPacketResender {

    protected ExponentialBackoffResendStrategyFactory exponentialResendStrategyFactory;

    public AbstractNetworkPacketResender() {
        this.exponentialResendStrategyFactory = new ExponentialBackoffResendStrategyFactory();
    }

    public abstract byte[] resendPacket() throws IOException;
}
