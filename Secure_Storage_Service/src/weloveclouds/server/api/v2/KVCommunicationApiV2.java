package weloveclouds.server.api.v2;

import static weloveclouds.client.monitoring.KVClientMonitoringMetricUtils.recordExecutionTime;
import static weloveclouds.client.monitoring.MonitoringMetricConstants.DELETE_COMMAND_NAME;
import static weloveclouds.client.monitoring.MonitoringMetricConstants.GET_COMMAND_NAME;
import static weloveclouds.client.monitoring.MonitoringMetricConstants.LATENCY;
import static weloveclouds.client.monitoring.MonitoringMetricConstants.PUT_COMMAND_NAME;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.api.IKVCommunicationApi;
import weloveclouds.server.api.v1.KVCommunicationApiV1;

/**
 * The implementation of the 2nd generation {@link IKVCommunicationApi}.
 *
 * @author Benedek
 */
public class KVCommunicationApiV2 implements IKVCommunicationApiV2 {

    private static final Logger LOGGER = Logger.getLogger(KVCommunicationApiV2.class);

    private KVCommunicationApiV1 communicationApi;

    private ServerConnectionInfo recentConnectionInfo;
    private RingMetadata metadata;

    /**
     * @param bootstrapConnectionInfo the initial connection information, which is used for deciding
     *        which server to connect to first (before having any {@link RingMetadata}
     */
    public KVCommunicationApiV2(ServerConnectionInfo bootstrapConnectionInfo) {
        this.communicationApi =
                new KVCommunicationApiV1(bootstrapConnectionInfo.getIpAddress().getHostAddress(),
                        bootstrapConnectionInfo.getPort());
        this.recentConnectionInfo = bootstrapConnectionInfo;
    }

    @Override
    public void connect() throws Exception {
        communicationApi.connect();
    }

    @Override
    public void disconnect() {
        communicationApi.disconnect();
    }

    @Override
    public IKVMessage put(String key, String value) throws Exception {
        connectToTheRightServerBasedOnHashFor(key);

        Instant start = Instant.now();
        try {
            return communicationApi.put(key, value);
        } finally {
            String commandName =
                    (value == null || value.isEmpty()) ? DELETE_COMMAND_NAME : PUT_COMMAND_NAME;
            recordExecutionTime(commandName, LATENCY, new Duration(start, Instant.now()));
        }
    }

    @Override
    public IKVMessage get(String key) throws Exception {
        connectToTheRightServerBasedOnHashFor(key);

        Instant start = Instant.now();
        try {
            return communicationApi.get(key);
        } finally {
            recordExecutionTime(GET_COMMAND_NAME, LATENCY, new Duration(start, Instant.now()));
        }
    }

    @Override
    public double getVersion() {
        return 2.0;
    }

    @Override
    public boolean isConnected() {
        return communicationApi.isConnected();
    }

    @Override
    public void connectTo(ServerConnectionInfo remoteServer) throws UnableToConnectException {
        try {
            communicationApi.connectTo(remoteServer);
            recentConnectionInfo = remoteServer;
        } catch (UnableToConnectException ex) {
            throw ex;
        }
    }

    @Override
    public void send(byte[] content) throws UnableToSendContentToServerException {
        communicationApi.send(content);
    }

    @Override
    public byte[] sendAndExpectForResponse(byte[] content) throws IOException {
        return communicationApi.sendAndExpectForResponse(content);
    }

    @Override
    public byte[] receive() throws ClientNotConnectedException, ConnectionClosedException {
        return communicationApi.receive();
    }

    @Override
    public ServerConnectionInfo getServerConnectionInfo() {
        return recentConnectionInfo;
    }

    @Override
    public void setRingMetadata(RingMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Looks for and connects to the server which is responsible for the respective key's hash
     * value.
     *
     * @param key of an entry (<key, value> pair) whose hash has to be calculated to decide which
     *        server to connect to
     */
    private void connectToTheRightServerBasedOnHashFor(String key) {
        Hash keyHash = HashingUtils.getHash(key);

        if (metadata != null) {
            RingMetadataPart serverMetadata = metadata.findServerInfoByHash(keyHash);
            if (serverMetadata != null) {
                ServerConnectionInfo connectionDetails = serverMetadata.getConnectionInfo();
                if (!(isConnected() && recentConnectionInfo.equals(connectionDetails))) {
                    disconnect();
                    try {
                        connectTo(connectionDetails);
                    } catch (UnableToConnectException ex) {
                        LOGGER.error(ex);
                    }
                }
            } else {
                LOGGER.error("No suitable server is found for the range.");

                if (!isConnected()) {
                    try {
                        LOGGER.debug(StringUtils.join(" ",
                                "Trying to connect with the most recent connection details:",
                                recentConnectionInfo));

                        connectTo(recentConnectionInfo);
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                    }
                }
            }
        } else {
            LOGGER.error("Server hash range metadata is empty.");
        }
    }
}
