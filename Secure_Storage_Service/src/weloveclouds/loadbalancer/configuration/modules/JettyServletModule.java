package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;

import weloveclouds.loadbalancer.rest.api.v1.resources.LoadBalancerServiceResource;
import weloveclouds.loadbalancer.rest.api.v1.resources.MonitoringServiceResource;

/**
 * Created by Benoit on 2017-01-23.
 */
public class JettyServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(GuiceFilter.class);
        bind(LoadBalancerServiceResource.class);
        bind(MonitoringServiceResource.class);
    }
}
