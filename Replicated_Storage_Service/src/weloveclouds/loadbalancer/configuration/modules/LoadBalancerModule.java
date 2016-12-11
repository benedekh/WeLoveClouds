package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.CacheMaximalCapacity;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.configuration.annotations.EcsNotificationServicePort;
import weloveclouds.loadbalancer.configuration.annotations.HealthMonitoringServicePort;
import weloveclouds.loadbalancer.configuration.providers.LoadBalancerConfigurationProvider;
import weloveclouds.loadbalancer.models.cache.ICache;
import weloveclouds.loadbalancer.models.cache.SimpleRequestCache;
import weloveclouds.loadbalancer.services.CacheService;
import weloveclouds.loadbalancer.services.ICacheService;

/**
 * Created by Benoit on 2016-12-04.
 */
public class LoadBalancerModule extends AbstractModule {

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

        bind(Integer.class).annotatedWith(EcsNotificationServicePort.class).toInstance
                (LoadBalancerConfigurationProvider.getEcsNotificationServicePort());

        bind(Integer.class).annotatedWith(CacheMaximalCapacity.class).toInstance
                (LoadBalancerConfigurationProvider.getCacheMaximalCapacity());

        bind(new TypeLiteral<ICache<String, String>>() {
        }).to(new TypeLiteral<SimpleRequestCache<String, String>>() {
        });

        bind(new TypeLiteral<ICacheService<String, String>>() {
        }).to(new TypeLiteral<CacheService>() {
        });
    }
}
