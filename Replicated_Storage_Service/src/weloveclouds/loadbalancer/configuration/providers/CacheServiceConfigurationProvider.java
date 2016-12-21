package weloveclouds.loadbalancer.configuration.providers;

import org.joda.time.Duration;

/**
 * Created by Benoit on 2016-12-21.
 */
public class CacheServiceConfigurationProvider {
    private static final int CACHE_MAXIMAL_CAPACITY = 200;
    private static final int POPULARITY_BASED_DISPLACEMENT_CAPACITY = 200;
    private static final Duration POPULARITY_BASED_DISPLACEMENT_RUN_INTERVAL = new Duration(10000);

    public static int getCacheMaximalCapacity() {
        return CACHE_MAXIMAL_CAPACITY;
    }

    public static int getPopularityBasedDisplacementCapacity() {
        return POPULARITY_BASED_DISPLACEMENT_CAPACITY;
    }

    public static Duration getPopularityBasedDisplacementRunInterval() {
        return POPULARITY_BASED_DISPLACEMENT_RUN_INTERVAL;
    }
}
