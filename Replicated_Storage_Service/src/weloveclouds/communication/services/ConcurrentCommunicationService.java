package weloveclouds.communication.services;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.communication.NetworkPacketResender;
import weloveclouds.commons.communication.NetworkPacketResenderFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.util.MessageFramesDetector;

/**
 * A communication service that can handle multiple connections concurrently.
 * 
 * @author Benoit
 */
public class ConcurrentCommunicationService implements IConcurrentCommunicationService {

    private static final int MAX_PACKET_SIZE_IN_BYTES = 65535;
    private static final int MAX_NUMBER_OF_RESEND_ATTEMPTS = 10;

    private NetworkPacketResenderFactory packetResenderFactory;
    private Map<Connection, MessageFramesDetector> messageFrameDetectorMap;

    /**
     * @param packetResenderFactory a factory to create resenders which overcome network errors
     */
    public ConcurrentCommunicationService(NetworkPacketResenderFactory packetResenderFactory) {
        this.packetResenderFactory = packetResenderFactory;
        this.messageFrameDetectorMap = new ConcurrentHashMap<>();
    }

    @Override
    public void send(byte[] message, Connection connection) throws IOException {
        NetworkPacketResender packetResender = packetResenderFactory
                .createResenderWithExponentialBackoff(MAX_NUMBER_OF_RESEND_ATTEMPTS,
                        new EncapsulatedCommunicationApi(connection, messageFrameDetectorMap),
                        message);
        packetResender.resendPacket();
    }

    @Override
    public byte[] sendAndExpectForResponse(byte[] content, Connection connection)
            throws IOException {
        NetworkPacketResender packetResender = packetResenderFactory
                .createResenderWithResponseWithExponentialBackoff(MAX_NUMBER_OF_RESEND_ATTEMPTS,
                        new EncapsulatedCommunicationApi(connection, messageFrameDetectorMap),
                        content);
        return packetResender.resendPacket();
    }

    @Override
    public byte[] receiveFrom(Connection connection) throws IOException {
        try {
            return new EncapsulatedCommunicationApi(connection, messageFrameDetectorMap).receive();
        } catch (ClientNotConnectedException | ConnectionClosedException ex) {
            throw new IOException(ex);
        }
    }

    private static class EncapsulatedCommunicationApi implements ICommunicationApi {

        private static final Logger LOGGER = Logger.getLogger(EncapsulatedCommunicationApi.class);
        private Map<Connection, MessageFramesDetector> messageFrameDetectorMap;
        private Connection connectionToEndpoint;

        public EncapsulatedCommunicationApi(Connection connectionToEndpoint,
                Map<Connection, MessageFramesDetector> messageFrameDetectorMap) {
            this.connectionToEndpoint = connectionToEndpoint;
            this.messageFrameDetectorMap = messageFrameDetectorMap;
        }

        @Override
        public double getVersion() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isConnected() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void connectTo(ServerConnectionInfo remoteServer) throws UnableToConnectException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void disconnect() throws UnableToDisconnectException {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] sendAndExpectForResponse(byte[] content) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void send(byte[] content) throws UnableToSendContentToServerException {
            try {
                if (connectionToEndpoint.isConnected()) {
                    LOGGER.debug("Getting output stream from the connection.");
                    OutputStream outputStream = connectionToEndpoint.getOutputStream();
                    LOGGER.debug("Sending message over the connection.");
                    outputStream.write(content);
                    outputStream.flush();
                    LOGGER.info("Message sent.");
                } else {
                    LOGGER.debug("Client is not connected, so message cannot be sent.");
                    throw new ClientNotConnectedException();
                }
            } catch (Exception ex) {
                throw new UnableToSendContentToServerException(ex.getMessage());
            }
        }

        @Override
        public byte[] receive() throws ClientNotConnectedException, ConnectionClosedException {
            try {
                String errorMessage = "Client is not connected, so message cannot be received.";

                if (!messageFrameDetectorMap.containsKey(connectionToEndpoint)) {
                    messageFrameDetectorMap.put(connectionToEndpoint, new MessageFramesDetector());
                }

                MessageFramesDetector messageDetector =
                        messageFrameDetectorMap.get(connectionToEndpoint);
                if (messageDetector.containsMessage()) {
                    return messageDetector.getMessage();
                }

                if (connectionToEndpoint.isConnected()) {
                    InputStream socketDataReader = connectionToEndpoint.getInputStream();

                    byte[] buffer = new byte[MAX_PACKET_SIZE_IN_BYTES];
                    int readBytes = 0;
                    ByteArrayOutputStream baosBuffer = new ByteArrayOutputStream();
                    while ((readBytes = socketDataReader.read(buffer)) > 0) {
                        if (readBytes < buffer.length) {
                            byte[] smaller = new byte[readBytes];
                            System.arraycopy(buffer, 0, smaller, 0, readBytes);
                            buffer = smaller;
                        }

                        LOGGER.debug(CustomStringJoiner.join(" ", "Received",
                                String.valueOf(readBytes), "bytes from the connection."));
                        baosBuffer.write(buffer);

                        byte[] contentReceivedSoFar = baosBuffer.toByteArray();
                        contentReceivedSoFar =
                                messageDetector.fillMessageQueue(contentReceivedSoFar);

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
            } catch (IOException ex) {
                throw new ClientNotConnectedException();
            }
        }
    }
}
