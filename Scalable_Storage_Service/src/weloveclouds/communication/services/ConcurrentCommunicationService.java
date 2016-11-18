package weloveclouds.communication.services;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.Connection;

/**
 * A communication service that can handle multiple connections concurrently.
 * 
 * @author Benoit
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    private static final Logger LOGGER = Logger.getLogger(ConcurrentCommunicationService.class);

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        if (connection.isConnected()) {
            LOGGER.debug("Getting output stream from the connection.");
            OutputStream outputStream = connection.getOutputStream();
            LOGGER.debug("Sending message over the connection.");
            outputStream.write(message);
            outputStream.flush();
            LOGGER.info("Message sent.");
        } else {
            String errorMessage = "Client is not connected, so message cannot be sent.";
            LOGGER.debug(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    @Override
    public byte[] receiveFrom(Connection connection) throws IOException {
        if (connection.isConnected()) {
            byte[] receivedData = null;
            InputStream socketDataReader = connection.getInputStream();

            while (receivedData == null) {
                int availableBytes = socketDataReader.available();
                if (availableBytes != 0) {
                    LOGGER.debug(CustomStringJoiner.join(" ", "Receiving",
                            String.valueOf(availableBytes), "from the connection."));
                    receivedData = new byte[availableBytes];
                    socketDataReader.read(receivedData);
                    LOGGER.debug("Data received from the network.");
                }
            }

            return receivedData;
        } else {
            String errorMessage = "Client is not connected, so message cannot be received.";
            LOGGER.debug(errorMessage);
            throw new IOException(errorMessage);
        }
    }

}
