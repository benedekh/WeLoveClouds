package weloveclouds.communication.services;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.communication.NetworkPacketResender;
import weloveclouds.commons.communication.NetworkPacketResenderFactory;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.CommunicationService.OnlySenderCommunicationApi;
import weloveclouds.communication.util.MessageFramesDetector;

/**
 * A communication service that can handle multiple connections concurrently.
 * 
 * @author Benoit
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    private static final int MAX_PACKET_SIZE_IN_BYTES = 65535;
    private static final int MAX_NUMBER_OF_RESEND_ATTEMPTS = 10;

    private static final Logger LOGGER = Logger.getLogger(ConcurrentCommunicationService.class);

    private NetworkPacketResenderFactory packetResenderFactory;
    private Map<Connection, MessageFramesDetector> messageFrameDetectorMap;

    /**
     * @param packetResenderFactory a factory to create resenders which overcome network errors
     */
    public ConcurrentCommunicationService(NetworkPacketResenderFactory packetResenderFactory) {
        messageFrameDetectorMap = new ConcurrentHashMap<>();
    }

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        NetworkPacketResender packetResender = packetResenderFactory
                .createResenderWithExponentialBackoff(MAX_NUMBER_OF_RESEND_ATTEMPTS,
                        new OnlySenderCommunicationApi(connection), message);
        packetResender.resendPacket();
    }

    @Override
    public byte[] receiveFrom(Connection connection) throws IOException {
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

                LOGGER.debug(CustomStringJoiner.join(" ", "Received", String.valueOf(readBytes),
                        "bytes from the connection."));
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
                LOGGER.debug(errorMessage);
                throw new IOException(errorMessage);
            } else {
                if (messageDetector.containsMessage()) {
                    return messageDetector.getMessage();
                } else {
                    return new byte[0];
                }
            }
        } else {
            LOGGER.debug(errorMessage);
            throw new IOException(errorMessage);
        }
    }

}
