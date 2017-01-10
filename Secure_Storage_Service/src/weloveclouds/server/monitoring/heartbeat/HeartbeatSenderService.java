package weloveclouds.server.monitoring.heartbeat;

import org.apache.log4j.Logger;

import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.IKVHeartbeatMessage;
import weloveclouds.loadbalancer.models.KVHeartbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Heartbeat sender service, which sends heartbeat message to the recipient (loadbalancer).
 * 
 * @author Benedek
 */
public class HeartbeatSenderService implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(HeartbeatSenderService.class);

    private ICommunicationApi communicationApi;
    private ServerConnectionInfo connectionInfo;
    private IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> heartbeatSerializer;

    protected HeartbeatSenderService() {}

    protected HeartbeatSenderService(Builder builder) {
        this.communicationApi = builder.communicationApi;
        this.connectionInfo = builder.connectionInfo;
        this.heartbeatSerializer = builder.heartbeatSerializer;
    }

    /**
     * Connect to the loadbalancer.
     * 
     * @throws UnableToConnectException if a connection error occurs
     */
    public void connect() throws UnableToConnectException {
        communicationApi.connectTo(connectionInfo);
    }

    /**
     * Sends the heartbeat message constructed from the health infos to the loadbalancer.
     * 
     * @throws UnableToSendContentToServerException if an error occurs
     */
    public void send(NodeHealthInfos healthInfos) throws UnableToSendContentToServerException {
        SerializedMessage message =
                heartbeatSerializer.serialize(new KVHeartbeatMessage(healthInfos));
        communicationApi.send(message.getBytes());
    }

    @Override
    public void close() {
        try {
            communicationApi.disconnect();
        } catch (UnableToDisconnectException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * A builder to create a {@link HeartbeatSenderService} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ICommunicationApi communicationApi;
        private ServerConnectionInfo connectionInfo;
        private IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> heartbeatSerializer;

        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder loadbalancerConnectionInfo(ServerConnectionInfo loadbalancerConnectionInfo) {
            this.connectionInfo = loadbalancerConnectionInfo;
            return this;
        }

        public Builder heartbeatSerializer(
                IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> heartbeatSerializer) {
            this.heartbeatSerializer = heartbeatSerializer;
            return this;
        }

        public HeartbeatSenderService build() {
            return new HeartbeatSenderService(this);
        }
    }

}
