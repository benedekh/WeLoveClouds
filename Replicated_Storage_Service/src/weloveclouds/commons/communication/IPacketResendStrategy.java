package weloveclouds.commons.communication;

import java.io.IOException;

import weloveclouds.communication.api.ICommunicationApi;

public interface IPacketResendStrategy {

    void configure(int attemptNumber);

    byte[] resendPacket(ICommunicationApi communicationApi, byte[] packet) throws IOException;
}
