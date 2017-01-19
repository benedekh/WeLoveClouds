package weloveclouds.loadbalancer.configuration;

import weloveclouds.ecs.exceptions.configuration.InvalidLoadBalancerConfigurationException;

/**
 * Created by Benoit on 2017-01-18.
 */
public class LoadBalancerConfiguration {
    private String host;
    private int ecsNotificationServicePort;
    private int healthMonitoringServicePort;
    private int clientRequestInterceptorPort;
    private int ecsNotificationResponsePort;

    LoadBalancerConfiguration(LoadBalancerConfigurationBuilder builder) {
        this.host = builder.host;
        this.ecsNotificationServicePort = builder.ecsNotificationServicePort;
        this.healthMonitoringServicePort = builder.healthMonitoringServicePort;
        this.clientRequestInterceptorPort = builder.clientRequestInterceptorPort;
        this.ecsNotificationResponsePort = builder.ecsNotificationResponsePort;
    }

    public String getHost() {
        return host;
    }

    public int getEcsNotificationServicePort() {
        return ecsNotificationServicePort;
    }

    public int getHealthMonitoringServicePort() {
        return healthMonitoringServicePort;
    }

    public int getClientRequestInterceptorPort() {
        return clientRequestInterceptorPort;
    }

    public int getEcsNotificationResponsePort() {
        return ecsNotificationResponsePort;
    }

    public static class LoadBalancerConfigurationBuilder {
        private String host;
        private int ecsNotificationServicePort;
        private int healthMonitoringServicePort;
        private int clientRequestInterceptorPort;
        private int ecsNotificationResponsePort;

        public LoadBalancerConfigurationBuilder host(String host) {
            this.host = host;
            return this;
        }

        public LoadBalancerConfigurationBuilder ecsNotificationServicePort(int ecsNotificationServicePort) {
            this.ecsNotificationServicePort = ecsNotificationServicePort;
            return this;
        }

        public LoadBalancerConfigurationBuilder healthMonitoringServicePort(int healthMonitoringServicePort) {
            this.healthMonitoringServicePort = healthMonitoringServicePort;
            return this;
        }

        public LoadBalancerConfigurationBuilder clientRequestInterceptorPort(int clientRequestInterceptorPort) {
            this.clientRequestInterceptorPort = clientRequestInterceptorPort;
            return this;
        }

        public LoadBalancerConfigurationBuilder ecsNotificationResponsePort(int ecsNotificationResponsePort) {
            this.ecsNotificationResponsePort = ecsNotificationResponsePort;
            return this;
        }

        public LoadBalancerConfiguration build() throws InvalidLoadBalancerConfigurationException {
            if (!isNullOrEmpty(host)) {
                return new LoadBalancerConfiguration(this);
            } else {
                throw new InvalidLoadBalancerConfigurationException();
            }
        }

        private boolean isNullOrEmpty(String value) {
            return value == null || value.isEmpty();
        }
    }
}
