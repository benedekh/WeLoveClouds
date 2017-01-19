package weloveclouds.commons.configuration.modules;

import com.google.inject.AbstractModule;

import weloveclouds.commons.configuration.annotations.EcsDnsName;
import weloveclouds.commons.configuration.annotations.LoadBalancerDnsName;
import weloveclouds.commons.configuration.providers.DnsNameProvider;

/**
 * Created by Benoit on 2017-01-18.
 */
public class DnsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class).annotatedWith(EcsDnsName.class)
                .toInstance(DnsNameProvider.getEcsDnsName());
        bind(String.class).annotatedWith(LoadBalancerDnsName.class)
                .toInstance(DnsNameProvider.getLoadBalancerDnsName());
    }
}
