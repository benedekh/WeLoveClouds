package weloveclouds.commons.communication;

import java.io.IOException;

import weloveclouds.communication.api.ICommunicationApi;

public class NetworkPacketResender {

    private IPacketResendStrategy resendStrategy;

    public NetworkPacketResender(int attemptNumber, IPacketResendStrategy resendStrategy) {
        this.resendStrategy.configure(attemptNumber);
    }

    public byte[] resendPacket(ICommunicationApi communicationApi, byte[] packet)
            throws IOException {
        return resendStrategy.resendPacket(communicationApi, packet);
    }
}
