package weloveclouds.loadbalancer.configuration.providers;

import org.joda.time.Duration;

/**
 * Created by Benoit on 2016-12-21.
 */
public class HealthMonitoringServiceConfigurationProvider {
    private static final Duration HEALTH_WATCHER_RUN_INTERVAL_IN_SEC = new Duration(5000);
    private static final Duration HEALTH_REPORTING_THRESHOLD = new Duration(10000);

    public static Duration getHealthWatcherRunInterval() {
        return HEALTH_WATCHER_RUN_INTERVAL_IN_SEC;
    }

    public static Duration getHealthReportingThreshold() {
        return HEALTH_REPORTING_THRESHOLD;
    }
}
