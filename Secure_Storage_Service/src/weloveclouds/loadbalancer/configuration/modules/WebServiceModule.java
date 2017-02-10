package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;

import weloveclouds.commons.configuration.annotations.JerseyResourcesConfigClass;
import weloveclouds.commons.configuration.annotations.WebServicePort;
import weloveclouds.loadbalancer.configuration.providers.WebServiceConfigurationProvider;

/**
 * Created by Benoit on 2017-01-23.
 */
public class WebServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(WebServicePort.class)
                .toInstance(WebServiceConfigurationProvider.getPort());

        bind(String.class).annotatedWith(JerseyResourcesConfigClass.class)
                .toInstance(WebServiceConfigurationProvider.getJerseyResourcesConfifClass());
    }
}
