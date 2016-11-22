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

    private static final int MAX_PACKET_SIZE_IN_BYTES = 65535;
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
        String errorMessage = "Client is not connected, so message cannot be received.";

        if (connection.isConnected()) {
            InputStream socketDataReader = connection.getInputStream();

            byte[] buffer = new byte[MAX_PACKET_SIZE_IN_BYTES];
            int readBytes = 0;
            if ((readBytes = socketDataReader.read(buffer)) > 0) {
                if (readBytes < buffer.length) {
                    byte[] smaller = new byte[readBytes];
                    System.arraycopy(buffer, 0, smaller, 0, readBytes);
                    buffer = smaller;
                }
                LOGGER.debug(CustomStringJoiner.join(" ", "Received", String.valueOf(readBytes),
                        "bytes from the connection."));
                return buffer;
            }

            if (readBytes == -1) {
                // connection was closed
                LOGGER.debug(errorMessage);
                throw new IOException(errorMessage);
            } else {
                return new byte[0];
            }
        } else {
            LOGGER.debug(errorMessage);
            throw new IOException(errorMessage);
        }
    }

}
