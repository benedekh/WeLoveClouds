package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;

import org.joda.time.Duration;

import weloveclouds.loadbalancer.configuration.annotations.HealthReportingThreshold;
import weloveclouds.loadbalancer.configuration.annotations.HealthWatcherInterval;
import weloveclouds.loadbalancer.configuration.providers.HealthMonitoringServiceConfigurationProvider;

/**
 * Created by Benoit on 2016-12-21.
 */
public class HealthMonitoringServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Duration.class).annotatedWith(HealthWatcherInterval.class)
                .toInstance(HealthMonitoringServiceConfigurationProvider
                        .getHealthWatcherRunInterval());

        bind(Duration.class).annotatedWith(HealthReportingThreshold.class)
                .toInstance(HealthMonitoringServiceConfigurationProvider
                        .getHealthReportingThreshold());
    }
}
