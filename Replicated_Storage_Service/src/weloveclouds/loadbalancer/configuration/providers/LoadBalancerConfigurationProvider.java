package weloveclouds.loadbalancer.configuration.providers;

/**
 * Created by Benoit on 2016-12-05.
 */
public class LoadBalancerConfigurationProvider {
    private static final int CLIENT_REQUESTS_INTERCEPTOR_PORT = 10000;
    private static final int HEALTH_MONITORING_SERVICE_PORT = 20000;

    public static int getClientInterceptorServicePort() {
        return CLIENT_REQUESTS_INTERCEPTOR_PORT;
    }

    public static int getHealthMonitoringServicePort() {
        return HEALTH_MONITORING_SERVICE_PORT;
    }
}
