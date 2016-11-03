package weloveclouds.communication.api.v1;

import java.io.IOException;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.IConcurrentCommunicationService;

/**
 * Communication API which is used by the {@link Server} to maintain connection with the different
 * clients and to send messages them and receive data from them through the
 * {@link IConcurrentCommunicationService}.
 * 
 * @author Benoit
 */
public class ConcurrentCommunicationApiV1 implements IConcurrentCommunicationApi {

    private IConcurrentCommunicationService communicationService;

    public ConcurrentCommunicationApiV1(IConcurrentCommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        communicationService.send(message, connection);
    }

    @Override
    public byte[] receiveFrom(Connection connection) throws IOException {
        return communicationService.receiveFrom(connection);
    }
}
