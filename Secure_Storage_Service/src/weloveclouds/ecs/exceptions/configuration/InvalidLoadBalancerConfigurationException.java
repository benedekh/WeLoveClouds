package weloveclouds.ecs.exceptions.configuration;

/**
 * Created by Benoit on 2017-01-18.
 */
public class InvalidLoadBalancerConfigurationException extends InvalidConfigurationException {
    public InvalidLoadBalancerConfigurationException() {
        super("Invalid load balancer configuration provided. An host, ecs port, client request " +
                "interceptor port and health monitoring service port should be provided.");
    }

    public InvalidLoadBalancerConfigurationException(String message) {
        super(message);
    }
}
