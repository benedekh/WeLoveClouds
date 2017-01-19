package weloveclouds.loadbalancer.configuration;

import java.net.InetAddress;

import weloveclouds.commons.networking.NetworkArgumentsValidator;

/**
 * Created by Benoit on 2017-01-18.
 */
public class LoadBalancerConfigurationFactory {
    private static final int CLIENT_REQUEST_INTERCEPTOR_PORT_INDEX = 0;
    private static final int HEALTH_MONITORING_SERVICE_PORT_INDEX = 1;
    private static final int ECS_NOTIFICATION_SERVICE_PORT_INDEX = 2;
    private static final int ECS_NOTIFICATION_RESPONSE_PORT = 3;
    private static final int REQUIRED_NUMBER_OF_ARGUMENTS = 4;

    public static LoadBalancerConfiguration createLoadBalancerConfigurationFromArgs(String[] args)
            throws IllegalArgumentException {
        if (args.length != REQUIRED_NUMBER_OF_ARGUMENTS) {
            throw new IllegalArgumentException("4 arguments are needed: <client request " +
                    "interceptor port> <health monitoring service port> <ecs notification service" +
                    " port>");
        }
        try {
            return new LoadBalancerConfiguration
                    .LoadBalancerConfigurationBuilder()
                    .host(InetAddress.getLocalHost().toString())
                    .clientRequestInterceptorPort(NetworkArgumentsValidator
                            .validateNetworkPort(args[CLIENT_REQUEST_INTERCEPTOR_PORT_INDEX]))
                    .ecsNotificationServicePort(NetworkArgumentsValidator
                            .validateNetworkPort(args[ECS_NOTIFICATION_SERVICE_PORT_INDEX]))
                    .healthMonitoringServicePort(NetworkArgumentsValidator
                            .validateNetworkPort(args[HEALTH_MONITORING_SERVICE_PORT_INDEX]))
                    .ecsNotificationResponsePort(NetworkArgumentsValidator
                            .validateNetworkPort(args[ECS_NOTIFICATION_RESPONSE_PORT]))
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
