package weloveclouds.server.monitoring;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.loadbalancer.models.IKVHeartbeatMessage;
import weloveclouds.loadbalancer.models.KVHeartbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

public class NodeHealthMonitor extends Thread {

    private static final Logger LOGGER = Logger.getLogger(NodeHealthMonitor.class);
    private static final Duration WAIT_TIME_BEFORE_HEARTBEAT = new Duration(2 * 1000);

    private NodeHealthInfos.Builder nodeHealthInfosBuilder;
    private List<ServiceHealthMonitor> serviceHealthMonitors;

    private ICommunicationApi communicationApi;
    private ServerConnectionInfo connectionInfo;
    private IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> heartbeatSerializer;

    public NodeHealthMonitor(Builder builder) {
        this.nodeHealthInfosBuilder = builder.nodeHealthInfosBuilder;
        this.communicationApi = builder.communicationApi;
        this.connectionInfo = builder.connectionInfo;
        this.heartbeatSerializer = builder.heartbeatSerializer;
        this.serviceHealthMonitors = builder.serviceHealthMonitors;
    }

    public void setNodeStatus(NodeStatus status) {
        nodeHealthInfosBuilder.nodeStatus(status);
    }

    @Override
    public void run() {
        do {
            try {
                communicationApi.connectTo(connectionInfo);
                for (ServiceHealthMonitor serviceHealthMonitor : serviceHealthMonitors) {
                    nodeHealthInfosBuilder
                            .addServiceHealtInfos(serviceHealthMonitor.getHealthInfos());
                }
                SerializedMessage message = heartbeatSerializer
                        .serialize(new KVHeartbeatMessage(nodeHealthInfosBuilder.build()));
                communicationApi.send(message.getBytes());
                nodeHealthInfosBuilder.clearServicesHealthInfos();
                Thread.sleep(WAIT_TIME_BEFORE_HEARTBEAT.getMillis());
            } catch (UnableToConnectException | UnableToSendContentToServerException ex) {
                LOGGER.error(ex);
            } catch (InterruptedException ex) {
                LOGGER.error(ex);
                Thread.currentThread().interrupt();
            } finally {
                try {
                    communicationApi.disconnect();
                } catch (UnableToDisconnectException ex) {
                    LOGGER.error(ex);
                }
            }
        } while (!Thread.currentThread().isInterrupted());
    }

    public static class Builder {
        private NodeHealthInfos.Builder nodeHealthInfosBuilder;
        private ICommunicationApi communicationApi;
        private ServerConnectionInfo connectionInfo;
        private IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> heartbeatSerializer;
        private List<ServiceHealthMonitor> serviceHealthMonitors;

        public Builder nodeHealthInfosBuilder(NodeHealthInfos.Builder nodeHealthInfosBuilder) {
            this.nodeHealthInfosBuilder = nodeHealthInfosBuilder;
            return this;
        }

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

        public Builder serviceHealthMonitors(List<ServiceHealthMonitor> serviceHealthMonitors) {
            this.serviceHealthMonitors = serviceHealthMonitors;
            return this;
        }

        public NodeHealthMonitor build() {
            return new NodeHealthMonitor(this);
        }
    }
}
