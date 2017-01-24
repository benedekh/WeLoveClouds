package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.configuration.providers.CacheServiceConfigurationProvider;
import weloveclouds.loadbalancer.models.cache.ICache;
import weloveclouds.loadbalancer.models.cache.SimpleRequestCache;
import weloveclouds.loadbalancer.services.CacheService;
import weloveclouds.loadbalancer.services.ICacheService;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.LFUStrategy;

/**
 * Created by Benoit on 2016-12-21.
 */
public class CacheServiceModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(Integer.class).annotatedWith(CacheMaximalCapacity.class)
                .toInstance(CacheServiceConfigurationProvider.getCacheMaximalCapacity());

        bind(new TypeLiteral<ICache<String, String>>() {
        })
                .to(new TypeLiteral<SimpleRequestCache<String, String>>() {
                });

        bind(new TypeLiteral<ICacheService<String, String>>() {
        })
                .to(new TypeLiteral<CacheService>() {
                });

        bind(new TypeLiteral<DisplacementStrategy<String>>() {
        })
                .to(new TypeLiteral<LFUStrategy>() {
                });
    }
}
