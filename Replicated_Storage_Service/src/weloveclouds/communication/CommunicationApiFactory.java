package weloveclouds.communication;

import weloveclouds.commons.communication.NetworkPacketResenderFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.communication.services.ConcurrentCommunicationService;

/**
 * Factory for creating {@link ICommunicationApiV1} and {@link IConcurrentCommunicationApi}
 * instances which are used for communication.
 * 
 * @author Benoit
 */
public class CommunicationApiFactory {

    public ICommunicationApi createCommunicationApiV1() {
        return new CommunicationApiV1(
                new CommunicationService(new ConnectionFactory(new SocketFactory())),
                new NetworkPacketResenderFactory());
    }

    public IConcurrentCommunicationApi createConcurrentCommunicationApiV1() {
        return new ConcurrentCommunicationApiV1(new ConcurrentCommunicationService(),
                new NetworkPacketResenderFactory());
    }

}
