package weloveclouds.commons.communication.resend.strategy;

public interface IPacketResendWithResponseStrategy extends IPacketResendStrategy {

    /**
     * @return the response packet that arrived after sending the packet that had to be sent over
     *         the network
     */
    byte[] getResponse();
}
