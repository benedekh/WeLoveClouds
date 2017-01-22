package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;

import weloveclouds.loadbalancer.configuration.annotations.WebServicePort;
import weloveclouds.loadbalancer.configuration.providers.WebServiceConfigurationProvider;

/**
 * Created by Benoit on 2017-01-22.
 */
public class WebServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(WebServicePort.class)
                .toInstance(WebServiceConfigurationProvider.getPort());
    }
}
