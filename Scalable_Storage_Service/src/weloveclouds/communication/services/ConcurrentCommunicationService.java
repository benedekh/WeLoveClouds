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

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        if (connection.isConnected()) {
            logDebug("Getting output stream from the connection.");
            OutputStream outputStream = connection.getOutputStream();
            logDebug("Sending message over the connection.");
            outputStream.write(message);
            outputStream.flush();
            logInfo("Message sent.");
        } else {
            String errorMessage = "Client is not connected, so message cannot be sent.";
            logDebug(errorMessage);
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
                    logDebug(CustomStringJoiner.join(" ", "Receiving",
                            String.valueOf(availableBytes), "from the connection."));
                    receivedData = new byte[availableBytes];
                    socketDataReader.read(receivedData);
                    logDebug("Data received from the network.");
                }
            }

            return receivedData;
        } else {
            String errorMessage = "Client is not connected, so message cannot be received.";
            logDebug(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    /**
     * Thread-safe logging of a message with DEBUG level.
     */
    private void logDebug(Object message) {
        synchronized (logger) {
            logger.debug(message);
        }
    }

    /**
     * Thread-safe logging of a message with INFO level.
     */
    private void logInfo(Object message) {
        synchronized (logger) {
            logger.info(message);
        }
    }
}
