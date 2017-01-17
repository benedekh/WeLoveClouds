package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import org.joda.time.Duration;

import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.configuration.annotations.PopularityBasedDisplacementCapacity;
import weloveclouds.loadbalancer.configuration.annotations.PopularityBasedDisplacementRunInterval;
import weloveclouds.loadbalancer.configuration.providers.CacheServiceConfigurationProvider;
import weloveclouds.loadbalancer.models.cache.ICache;
import weloveclouds.loadbalancer.models.cache.SimpleRequestCache;
import weloveclouds.loadbalancer.models.cache.strategies.IDisplacementStrategy;
import weloveclouds.loadbalancer.models.cache.strategies.PopularityBasedDisplacementStrategy;
import weloveclouds.loadbalancer.services.CacheService;
import weloveclouds.loadbalancer.services.ICacheService;

/**
 * Created by Benoit on 2016-12-21.
 */
public class CacheServiceModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(Integer.class).annotatedWith(CacheMaximalCapacity.class)
                .toInstance(CacheServiceConfigurationProvider.getCacheMaximalCapacity());

        bind(Duration.class).annotatedWith(PopularityBasedDisplacementRunInterval.class)
                .toInstance(CacheServiceConfigurationProvider
                        .getPopularityBasedDisplacementRunInterval());

        bind(Integer.class).annotatedWith(PopularityBasedDisplacementCapacity.class)
                .toInstance(CacheServiceConfigurationProvider
                        .getPopularityBasedDisplacementCapacity());

        bind(new TypeLiteral<ICache<String, String>>() {
        })
                .to(new TypeLiteral<SimpleRequestCache<String, String>>() {
                });

        bind(new TypeLiteral<ICacheService<String, String>>() {
        })
                .to(new TypeLiteral<CacheService>() {
                });

        bind(new TypeLiteral<IDisplacementStrategy<String>>() {
        })
                .to(new TypeLiteral<PopularityBasedDisplacementStrategy<String>>() {
                });
    }
}
