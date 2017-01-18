package weloveclouds.ecs.models.repository;

import java.net.UnknownHostException;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.messaging.notification.INotifiable;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.ecs.models.repository.NodeStatus.HALTED;
import static weloveclouds.ecs.models.repository.NodeStatus.IDLE;

/**
 * Created by Benoit on 2017-01-06.
 */
public class LoadBalancer extends AbstractNode implements INotifiable {
    private ServerConnectionInfo healthMonitoringServiceEndpoint;
    private ServerConnectionInfo notificationServiceEndpoint;

    protected LoadBalancer(Builder builder) throws UnknownHostException {
        this.status = IDLE;
        this.name = builder.name;
        this.serverConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(builder.host)
                .port(builder.clientRequestInterceptorPort)
                .build();
        this.healthMonitoringServiceEndpoint = new ServerConnectionInfo.Builder()
                .ipAddress(builder.host)
                .port(builder.healthMonitoringServicePort)
                .build();
        this.notificationServiceEndpoint = new ServerConnectionInfo.Builder()
                .ipAddress(builder.host)
                .port(builder.ecsNotificationServicePort)
                .build();
        if (builder.healthInfos == null) {
            this.healthInfos = new NodeHealthInfos.Builder()
                    .nodeName(name)
                    .nodeStatus(HALTED)
                    .build();
        } else {
            this.healthInfos = builder.healthInfos;
        }
    }

    public ServerConnectionInfo getHealthMonitoringServiceEndpoint() {
        return healthMonitoringServiceEndpoint;
    }

    @Override
    public ServerConnectionInfo getNotificationServiceEndpoint() {
        return notificationServiceEndpoint;
    }

    @Override
    public ServerConnectionInfo getEcsChannelConnectionInfo() {
        return notificationServiceEndpoint;
    }

    @Override
    public String getIpAddress() {
        return serverConnectionInfo.getIpAddress().getCanonicalHostName();
    }

    @Override
    public String toString() {
        return StringUtils.join(" ",
                "Name:", this.name, "\n",
                "Status", status.name(), "\n",
                "Client request interceptor endpoint:", serverConnectionInfo.toString(), "\n",
                "Notification service endpoint:", notificationServiceEndpoint, "\n",
                "Health monitoring service endpoint:", healthMonitoringServiceEndpoint);
    }

    public static class Builder {
        private String name;
        private String host;
        private int ecsNotificationServicePort;
        private int healthMonitoringServicePort;
        private int clientRequestInterceptorPort;
        private NodeHealthInfos healthInfos;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder ecsNotificationServicePort(int ecsNotificationServicePort) {
            this.ecsNotificationServicePort = ecsNotificationServicePort;
            return this;
        }

        public Builder healthMonitoringServicePort(int healthMonitoringServicePort) {
            this.healthMonitoringServicePort = healthMonitoringServicePort;
            return this;
        }

        public Builder clientRequestInterceptorPort(int clientRequestInterceptorPort) {
            this.clientRequestInterceptorPort = clientRequestInterceptorPort;
            return this;
        }

        public Builder healthInfos(NodeHealthInfos nodeHealthInfos) {
            this.healthInfos = nodeHealthInfos;
            return this;
        }

        public LoadBalancer build() throws UnknownHostException {
            return new LoadBalancer(this);
        }
    }
}
