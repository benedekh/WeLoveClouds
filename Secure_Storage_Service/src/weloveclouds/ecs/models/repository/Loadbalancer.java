package weloveclouds.ecs.models.repository;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.ecs.models.messaging.notification.INotifiable;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.ecs.models.repository.NodeStatus.HALTED;
import static weloveclouds.ecs.models.repository.NodeStatus.IDLE;

/**
 * Created by Benoit on 2017-01-06.
 */
public class Loadbalancer extends AbstractNode implements INotifiable {
    private ServerConnectionInfo healthMonitoringServiceEndpoint;
    private ServerConnectionInfo notificationServiceEndpoint;

    public Loadbalancer(AbstractNode node) {
        this.status = IDLE;
        this.name = node.getName();
        this.serverConnectionInfo = node.getServerConnectionInfo();
        this.ecsChannelConnectionInfo = node.getEcsChannelConnectionInfo();
        this.notificationServiceEndpoint = new ServerConnectionInfo.Builder()
                .ipAddress(node.getServerConnectionInfo().getIpAddress())
                .port(30000)
                .build();
        this.hashKey = node.getHashKey();
        this.healthInfos = node.getHealthInfos();
    }

    protected Loadbalancer(Builder builder) {
        this.status = IDLE;
        this.name = builder.name;
        this.serverConnectionInfo = builder.clientRequestInterceptorEndpoint;
        this.ecsChannelConnectionInfo = builder.ecsChannelConnectionInfo;
        this.healthMonitoringServiceEndpoint = builder.healthMonitoringServiceEndpoint;
        this.notificationServiceEndpoint = builder.notificationServiceEndpoint;
        this.hashKey = builder.hashKey;
        if (builder.healthInfos == null) {
            this.healthInfos = new NodeHealthInfos.Builder()
                    .nodeName(name)
                    .nodeStatus(HALTED)
                    .build();
        } else {
            this.healthInfos = builder.healthInfos;
        }
    }

    @Override
    public ServerConnectionInfo getNotificationServiceEndpoint() {
        return notificationServiceEndpoint;
    }

    public static class Builder {
        private String name;
        private ServerConnectionInfo clientRequestInterceptorEndpoint;
        private ServerConnectionInfo ecsChannelConnectionInfo;
        private ServerConnectionInfo healthMonitoringServiceEndpoint;
        private ServerConnectionInfo notificationServiceEndpoint;
        private NodeHealthInfos healthInfos;
        private Hash hashKey;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder clientRequestInterceptorEndpoint(ServerConnectionInfo
                                                                clientRequestInterceptorEndpoint) {
            this.clientRequestInterceptorEndpoint = clientRequestInterceptorEndpoint;
            this.ecsChannelConnectionInfo = new ServerConnectionInfo.Builder()
                    .ipAddress(clientRequestInterceptorEndpoint.getIpAddress())
                    .port(ExternalConfigurationServiceConstants.ECS_REQUESTS_PORT).build();
            this.hashKey = HashingUtils.getHash(clientRequestInterceptorEndpoint.toString());
            return this;
        }

        public Builder healthMonitoringServiceEndpoint(ServerConnectionInfo
                                                               healthMonitoringServiceEndpoint) {
            this.healthMonitoringServiceEndpoint = healthMonitoringServiceEndpoint;
            return this;
        }

        public Builder notificationServiceEndpoint(ServerConnectionInfo
                                                           notificationServiceEndpoint) {
            this.notificationServiceEndpoint = notificationServiceEndpoint;
            return this;
        }

        public Builder healthInfos(NodeHealthInfos nodeHealthInfos) {
            this.healthInfos = nodeHealthInfos;
            return this;
        }

        public Loadbalancer build() {
            return new Loadbalancer(this);
        }
    }
}
