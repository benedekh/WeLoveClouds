package weloveclouds.ecs.configuration.modules;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;

import weloveclouds.ecs.rest.api.v1.resouces.DistributedServiceResource;
import weloveclouds.ecs.rest.api.v1.resouces.ExternalConfigurationServiceResource;
import weloveclouds.loadbalancer.rest.api.v1.resources.LoadBalancerServiceResource;
import weloveclouds.loadbalancer.rest.api.v1.resources.MonitoringServiceResource;

/**
 * Created by Benoit on 2017-01-23.
 */
public class JettyServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(GuiceFilter.class);
        bind(ExternalConfigurationServiceResource.class);
        bind(DistributedServiceResource.class);
    }
}
