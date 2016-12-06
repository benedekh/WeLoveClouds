package weloveclouds.server.api.v2;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import static app_kvClient.KVClient.*;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.monitoring.models.Service;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.models.messages.IKVMessage;
import weloveclouds.server.api.IKVCommunicationApi;
import weloveclouds.server.api.v1.KVCommunicationApiV1;

/**
 * The implementation of the 2nd generation {@link IKVCommunicationApi}.
 *
 * @author Benedek
 */
public class KVCommunicationApiV2 implements IKVCommunicationApiV2 {

    private static final Logger LOGGER = Logger.getLogger(KVCommunicationApiV2.class);
    private static final IStatsdClient STATSD_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

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
            Instant end = Instant.now();
            STATSD_CLIENT.recordExecutionTime(
                    new Metric.Builder().service(Service.KV_CLIENT)
                            .name(Arrays.asList(clientName, "put", "latency")).build(),
                    new Duration(start, end));
        }
    }

    @Override
    public IKVMessage get(String key) throws Exception {
        connectToTheRightServerBasedOnHashFor(key);

        Instant start = Instant.now();
        try {
            return communicationApi.get(key);
        } finally {
            Instant end = Instant.now();
            STATSD_CLIENT.recordExecutionTime(
                    new Metric.Builder().service(Service.KV_CLIENT)
                            .name(Arrays.asList(clientName, "get", "latency")).build(),
                    new Duration(start, end));
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
        Hash keyHash = HashingUtil.getHash(key);

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
                        LOGGER.debug(CustomStringJoiner.join(" ",
                                "Trying to connect with the most recent connection details:",
                                recentConnectionInfo.toString()));

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
