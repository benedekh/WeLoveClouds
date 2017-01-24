package weloveclouds.loadbalancer.configuration.providers;

import weloveclouds.loadbalancer.configuration.LoadBalancerConfigurationHolder;

/**
 * Created by Benoit on 2016-12-05.
 */
public class LoadBalancerConfigurationProvider {
    public static int getClientInterceptorServicePort() {
        return LoadBalancerConfigurationHolder.getLoadBalancerConfiguration()
                .getClientRequestInterceptorPort();
    }

    public static int getHealthMonitoringServicePort() {
        return LoadBalancerConfigurationHolder.getLoadBalancerConfiguration()
                .getHealthMonitoringServicePort();
    }

    public static int getEcsNotificationServicePort() {
        return LoadBalancerConfigurationHolder.getLoadBalancerConfiguration()
                .getEcsNotificationServicePort();
    }

    public static int getEcsNotificationResponsePort() {
        return LoadBalancerConfigurationHolder.getLoadBalancerConfiguration()
                .getEcsNotificationResponsePort();
    }
}
