package weloveclouds.communication.api.v1;

import java.io.IOException;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.IConcurrentCommunicationService;

/**
 * Created by Benoit on 2016-11-01.
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
