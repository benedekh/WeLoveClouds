package weloveclouds.communication.api.v2;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;
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

public class KVCommunicationApiV2 implements IKVCommunicationApiV2 {

    private KVCommunicationApiV1 communicationApi;

    private ServerConnectionInfo recentConnectionInfo;
    private RingMetadata metadata;

    private Logger logger;

    public KVCommunicationApiV2(ServerConnectionInfo bootstrapConnectionInfo) {
        this.communicationApi =
                new KVCommunicationApiV1(bootstrapConnectionInfo.getIpAddress().getHostAddress(),
                        bootstrapConnectionInfo.getPort());
        this.recentConnectionInfo = bootstrapConnectionInfo;
        this.logger = Logger.getLogger(getClass());
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
        return communicationApi.put(key, value);
    }

    @Override
    public IKVMessage get(String key) throws Exception {
        connectToTheRightServerBasedOnHashFor(key);
        return communicationApi.get(key);
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
                        logger.error(ex);
                    }
                }
            } else {
                logger.error("No suitable server is found for the range.");

                if (!isConnected()) {
                    try {
                        logger.debug(CustomStringJoiner.join(" ",
                                "Trying to connect with the most recent connection details:",
                                recentConnectionInfo.toString()));

                        connectTo(recentConnectionInfo);
                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                }
            }
        } else {
            logger.error("Server hash range metadata is empty.");
        }

    }

}
