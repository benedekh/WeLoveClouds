package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.configuration.annotations.HealthMonitoringServicePort;
import weloveclouds.loadbalancer.configuration.providers.LoadBalancerConfigurationProvider;
import weloveclouds.loadbalancer.models.cache.ICache;
import weloveclouds.loadbalancer.models.cache.SimpleRequestCache;
import weloveclouds.loadbalancer.services.CacheService;
import weloveclouds.loadbalancer.services.ICacheService;

/**
 * Created by Benoit on 2016-12-04.
 */
public class LoadbalancerModule extends AbstractModule {

    @Provides
    public IMessageSerializer<SerializedMessage, KVMessage> getClientMessageSerializer() {
        return new KVMessageSerializer();
    }

    @Provides
    public IMessageDeserializer<KVMessage, SerializedMessage> getClientMessageDeserializer() {
        return new KVMessageDeserializer();
    }

    @Provides
    public IMessageSerializer<SerializedMessage, KVAdminMessage> getAdminMessageSerializer() {
        return new KVAdminMessageSerializer();
    }

    @Provides
    public IMessageDeserializer<KVAdminMessage, SerializedMessage> getAdminMessageDeserializer() {
        return new KVAdminMessageDeserializer();
    }


    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(ClientRequestsInterceptorPort.class).toInstance
                (LoadBalancerConfigurationProvider.getClientInterceptorServicePort());

        bind(Integer.class).annotatedWith(HealthMonitoringServicePort.class).toInstance
                (LoadBalancerConfigurationProvider.getHealthMonitoringServicePort());

        bind(Integer.class).annotatedWith(CacheMaximalCapacity.class).toInstance
                (LoadBalancerConfigurationProvider.getCacheMaximalCapacity());

        bind(new TypeLiteral<ICache<String, String>>() {
        }).to(new TypeLiteral<SimpleRequestCache<String,String>>() {});

        bind(new TypeLiteral<ICacheService<String, String>>() {
        }).to(new TypeLiteral<CacheService>() {});
    }
}
