package weloveclouds.communication.api.v1;

import java.io.IOException;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.IConcurrentCommunicationService;
import weloveclouds.communication.services.resend.NetworkPacketResenderFactory;
import weloveclouds.server.core.Server;

/**
 * Communication API which is used by the {@link Server} to maintain connection with the different
 * clients and to send messages them and receive data from them through the
 * {@link IConcurrentCommunicationService}.
 * 
 * @author Benoit
 */
public class ConcurrentCommunicationApiV1 implements IConcurrentCommunicationApi {

    private static final int MAX_NUMBER_OF_RESEND_ATTEMPTS = 10;

    private IConcurrentCommunicationService communicationService;
    private NetworkPacketResenderFactory resenderFactory;

    public ConcurrentCommunicationApiV1(IConcurrentCommunicationService communicationService,
            NetworkPacketResenderFactory resenderFactory) {
        this.communicationService = communicationService;
        this.resenderFactory = resenderFactory;
    }

    @Override
    public void send(byte[] message, Connection<?> connection) throws IOException {
        resenderFactory.createResenderWithExponentialBackoff(MAX_NUMBER_OF_RESEND_ATTEMPTS, message)
                .sendWith(communicationService, connection);
    }

    @Override
    public byte[] sendAndExpectForResponse(byte[] content, Connection<?> connection)
            throws IOException {
        return resenderFactory.createResenderWithResponseWithExponentialBackoff(
                MAX_NUMBER_OF_RESEND_ATTEMPTS, content).sendWith(communicationService, connection);
    }

    @Override
    public byte[] receiveFrom(Connection<?> connection) throws IOException {
        return communicationService.receiveFrom(connection);
    }
}
