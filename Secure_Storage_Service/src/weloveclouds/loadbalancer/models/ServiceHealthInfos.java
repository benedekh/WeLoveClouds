package weloveclouds.loadbalancer.models;

import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2016-12-05.
 */
public class ServiceHealthInfos {
    private String serviceName;
    private ServiceStatus serviceStatus;
    private ServerConnectionInfo serviceEndpoint;
    private int numberOfActiveConnections;
    private int servicePriority;

    protected ServiceHealthInfos(Builder builder) {
        this.serviceName = builder.serviceName;
        this.serviceStatus = builder.serviceStatus;
        this.serviceEndpoint = builder.serviceEndpoint;
        this.numberOfActiveConnections = builder.numberOfActiveConnections;
        this.servicePriority = builder.servicePriority;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public ServerConnectionInfo getServiceEndpoint() {
        return serviceEndpoint;
    }

    public int getNumberOfActiveConnections() {
        return numberOfActiveConnections;
    }

    public int getServicePriority() {
        return servicePriority;
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "{Service name:", serviceName, ", Service status:",
                serviceStatus, ", Service endpoint:", serviceEndpoint,
                ", Number of active connections:", numberOfActiveConnections, ", Service priority:",
                servicePriority, "}");
    }

    public static class Builder {
        private String serviceName;
        private ServiceStatus serviceStatus;
        private ServerConnectionInfo serviceEndpoint;
        private int numberOfActiveConnections;
        private int servicePriority;

        public Builder serviceName(String serverName) {
            this.serviceName = serverName;
            return this;
        }

        public Builder serviceStatus(ServiceStatus serviceStatus) {
            this.serviceStatus = serviceStatus;
            return this;
        }

        public Builder serviceEnpoint(ServerConnectionInfo serviceEndpoint) {
            this.serviceEndpoint = serviceEndpoint;
            return this;
        }

        public Builder numberOfActiveConnections(int numberOfActiveConnections) {
            this.numberOfActiveConnections = numberOfActiveConnections;
            return this;
        }

        public Builder servicePriority(int servicePriority) {
            this.servicePriority = servicePriority;
            return this;
        }

        public ServiceHealthInfos build() {
            return new ServiceHealthInfos(this);
        }
    }
}
