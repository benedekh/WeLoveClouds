package weloveclouds.communication.services;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import weloveclouds.communication.models.Connection;

/**
 * Created by Benoit on 2016-10-31.
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(message);
        outputStream.flush();
    }

    @Override
    public byte[] receiveFrom(Connection connection) throws IOException {
        byte[] receivedData = null;

        InputStream socketDataReader = connection.getInputStream();

        while (receivedData == null) {
            int availableBytes = socketDataReader.available();
            if (availableBytes != 0) {
                receivedData = new byte[availableBytes];
                socketDataReader.read(receivedData);
            }
        }
        return receivedData;
    }
}
