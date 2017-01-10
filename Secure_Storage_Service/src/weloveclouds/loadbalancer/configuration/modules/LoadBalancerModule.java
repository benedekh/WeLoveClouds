package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;

import weloveclouds.commons.serialization.configuration.modules.SerializationModule;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationServicePort;
import weloveclouds.loadbalancer.configuration.annotations.HealthMonitoringServicePort;
import weloveclouds.loadbalancer.configuration.providers.LoadBalancerConfigurationProvider;

/**
 * Created by Benoit on 2016-12-04.
 */
public class LoadBalancerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(ClientRequestsInterceptorPort.class)
                .toInstance(LoadBalancerConfigurationProvider.getClientInterceptorServicePort());

        bind(Integer.class).annotatedWith(HealthMonitoringServicePort.class)
                .toInstance(LoadBalancerConfigurationProvider.getHealthMonitoringServicePort());

        bind(Integer.class).annotatedWith(EcsNotificationServicePort.class)
                .toInstance(LoadBalancerConfigurationProvider.getEcsNotificationServicePort());

        install(new CacheServiceModule());
        install(new HealthMonitoringServiceModule());
        install(new SerializationModule());
    }
}
