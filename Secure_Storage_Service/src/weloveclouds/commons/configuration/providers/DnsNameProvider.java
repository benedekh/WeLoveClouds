package weloveclouds.commons.configuration.providers;

/**
 * Created by Benoit on 2017-01-18.
 */
public class DnsNameProvider {
    private final static String LOAD_BALANCER_DNS_NAME = "weloveclouds-lb.com";
    private final static String ECS_DNS_NAME = "weloveclouds-ecs.com";

    public static String getLoadBalancerDnsName() {
        return LOAD_BALANCER_DNS_NAME;
    }

    public static String getEcsDnsName() {
        return ECS_DNS_NAME;
    }
}
