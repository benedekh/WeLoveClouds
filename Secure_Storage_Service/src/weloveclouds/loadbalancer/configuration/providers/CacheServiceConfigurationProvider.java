package weloveclouds.loadbalancer.configuration.providers;

/**
 * Created by Benoit on 2016-12-21.
 */
public class CacheServiceConfigurationProvider {
    private static final int CACHE_MAXIMAL_CAPACITY = 1000;

    public static int getCacheMaximalCapacity() {
        return CACHE_MAXIMAL_CAPACITY;
    }
}
