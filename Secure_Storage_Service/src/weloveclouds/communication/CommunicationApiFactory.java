package weloveclouds.communication;

import weloveclouds.commons.networking.socket.client.SSLSocketFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.communication.models.factory.SecureConnectionFactory;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.communication.services.resend.NetworkPacketResenderFactory;

/**
 * Factory for creating {@link ICommunicationApiV1} and {@link IConcurrentCommunicationApi}
 * instances which are used for communication.
 *
 * @author Benoit
 */
public class CommunicationApiFactory {

    /**
     * Creates a {@link CommunicationApiV1} instance.
     */
    public ICommunicationApi createCommunicationApiV1() {
        return new CommunicationApiV1(
                new CommunicationService(new SecureConnectionFactory(new SSLSocketFactory())),
                new NetworkPacketResenderFactory());
    }

    /**
     * Creates a {@link ConcurrentCommunicationApiV1} instance.
     */
    public IConcurrentCommunicationApi createConcurrentCommunicationApiV1() {
        return new ConcurrentCommunicationApiV1(new ConcurrentCommunicationService(),
                new NetworkPacketResenderFactory());
    }
}
