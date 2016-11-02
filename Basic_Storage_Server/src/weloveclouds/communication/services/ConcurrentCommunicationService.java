package weloveclouds.communication.services;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.Connection;

/**
 * Created by Benoit on 2016-10-31.
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        if (connection.isConnected()) {
            logger.debug("Getting output stream from the connection.");
            OutputStream outputStream = connection.getOutputStream();
            logger.debug("Sending message over the connection.");
            outputStream.write(message);
            outputStream.flush();
            logger.info("Message sent.");
        } else {
            String errorMessage = "Client is not connected, so message cannot be sent.";
            logger.debug(errorMessage);
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
                    logger.debug(CustomStringJoiner.join(" ", "Receiving",
                            String.valueOf(availableBytes), "from the connection."));
                    receivedData = new byte[availableBytes];
                    socketDataReader.read(receivedData);
                    logger.debug("Data received from the network.");
                }
            }

            return receivedData;
        } else {
            String errorMessage = "Client is not connected, so message cannot be received.";
            logger.debug(errorMessage);
            throw new IOException(errorMessage);
        }
    }
}
