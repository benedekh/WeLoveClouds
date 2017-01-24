package weloveclouds.communication.services;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.utils.MessageFramesDetector;

/**
 * A communication service that can handle multiple connections concurrently.
 * 
 * @author Benoit, Benedek
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    private static final int MAX_PACKET_SIZE_IN_BYTES = 65535;
    private static final Logger LOGGER = Logger.getLogger(ConcurrentCommunicationService.class);

    private Map<Connection<?>, MessageFramesDetector> messageFrameDetectorMap;

    public ConcurrentCommunicationService() {
        this.messageFrameDetectorMap = new ConcurrentHashMap<>();
    }

    @Override
    public void send(byte[] message, Connection<?> connection) throws IOException {
        try {
            if (connection.isConnected()) {
                LOGGER.debug("Getting output stream from the connection.");
                OutputStream outputStream = connection.getOutputStream();
                LOGGER.debug("Sending message over the connection.");
                outputStream.write(message);
                outputStream.flush();
                LOGGER.info("Message sent.");
            } else {
                throw new ClientNotConnectedException();
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new IOException("Client is not connected, so message cannot be sent.");
        }
    }

    @Override
    public byte[] receiveFrom(Connection<?> connection) throws IOException {
        String errorMessage = "Client is not connected, so message cannot be received.";

        if (!messageFrameDetectorMap.containsKey(connection)) {
            messageFrameDetectorMap.put(connection, new MessageFramesDetector());
        }

        MessageFramesDetector messageDetector = messageFrameDetectorMap.get(connection);
        if (messageDetector.containsMessage()) {
            return messageDetector.getMessage();
        }

        if (connection.isConnected()) {
            InputStream socketDataReader = connection.getInputStream();

            byte[] buffer = new byte[MAX_PACKET_SIZE_IN_BYTES];
            int readBytes = 0;
            ByteArrayOutputStream baosBuffer = new ByteArrayOutputStream();
            while ((readBytes = socketDataReader.read(buffer)) > 0) {
                if (readBytes < buffer.length) {
                    byte[] smaller = new byte[readBytes];
                    System.arraycopy(buffer, 0, smaller, 0, readBytes);
                    buffer = smaller;
                }

                LOGGER.debug(
                        StringUtils.join(" ", "Received", readBytes, "bytes from the connection."));
                baosBuffer.write(buffer);

                byte[] contentReceivedSoFar = baosBuffer.toByteArray();
                contentReceivedSoFar = messageDetector.fillMessageQueue(contentReceivedSoFar);

                if (messageDetector.containsMessage()) {
                    baosBuffer.reset();
                    baosBuffer.write(contentReceivedSoFar);

                    return messageDetector.getMessage();
                } else {
                    baosBuffer.reset();
                    baosBuffer.write(contentReceivedSoFar);
                }
            }

            if (readBytes == -1) {
                // connection was closed
                throw new IOException(errorMessage);
            } else {
                if (messageDetector.containsMessage()) {
                    return messageDetector.getMessage();
                } else {
                    return new byte[0];
                }
            }
        } else {
            throw new IOException(errorMessage);
        }
    }

}
