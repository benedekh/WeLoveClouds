package weloveclouds.ecs.configuration.providers;

import org.joda.time.Duration;

/**
 * Created by Benoit on 2017-01-27.
 */
public class TaskServiceConfigurationProvider {
    private static final Duration MINIMUM_INTERVAL_BETWEEN_RETRY = new Duration(300);

    public static Duration getMinimumIntervalBetweenRetry() {
        return MINIMUM_INTERVAL_BETWEEN_RETRY;
    }
}
