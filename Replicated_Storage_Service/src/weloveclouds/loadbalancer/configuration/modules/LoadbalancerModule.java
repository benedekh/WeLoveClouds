package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.configuration.annotations.ClientRequestsInterceptorPort;
import weloveclouds.loadbalancer.configuration.annotations.HealthMonitoringServicePort;
import weloveclouds.loadbalancer.configuration.providers.LoadBalancerConfigurationProvider;

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
    }
}
