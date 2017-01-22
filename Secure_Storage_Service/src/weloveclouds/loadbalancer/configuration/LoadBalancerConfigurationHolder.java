package weloveclouds.loadbalancer.configuration;

/**
 * Created by Benoit on 2017-01-18.
 */
public class LoadBalancerConfigurationHolder {
    private static LoadBalancerConfiguration loadBalancerConfiguration;

    public static LoadBalancerConfiguration getLoadBalancerConfiguration() {
        return loadBalancerConfiguration;
    }

    public static void register(LoadBalancerConfiguration loadBalancerConfigurationToRegister) {
        loadBalancerConfiguration = loadBalancerConfigurationToRegister;
    }
}
