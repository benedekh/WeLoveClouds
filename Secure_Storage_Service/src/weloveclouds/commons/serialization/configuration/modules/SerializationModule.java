package weloveclouds.commons.serialization.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import weloveclouds.commons.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.KVHeartbeatMessageSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.serialization.deserialization.KVHeartbeatMessageDeserializer;
import weloveclouds.commons.serialization.deserialization.NodeHealtInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.ServiceHealthInfosDeserializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.IKVHeartbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * Guice module for serialization.
 * 
 * @author Benoit
 */
public class SerializationModule extends AbstractModule {
    @Override
    protected void configure() {
        bindSerializers();
        bindDeserializers();
    }

    private void bindSerializers() {
        bind(new TypeLiteral<IMessageSerializer<SerializedMessage, IKVMessage>>() {})
                .to(new TypeLiteral<KVMessageSerializer>() {});

        bind(new TypeLiteral<IMessageSerializer<SerializedMessage, IKVAdminMessage>>() {})
                .to(new TypeLiteral<KVAdminMessageSerializer>() {});

        bind(new TypeLiteral<IMessageSerializer<SerializedMessage, IKVMessage>>() {})
                .to(new TypeLiteral<KVMessageSerializer>() {});

        bind(new TypeLiteral<IMessageSerializer<SerializedMessage, IKVHeartbeatMessage>>() {})
                .to(new TypeLiteral<KVHeartbeatMessageSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, NodeHealthInfos>>() {})
                .to(new TypeLiteral<NodeHealthInfosSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, ServiceHealthInfos>>() {})
                .to(new TypeLiteral<ServiceHealthInfosSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, ServerConnectionInfo>>() {})
                .to(new TypeLiteral<ServerConnectionInfoSerializer>() {});
    }

    private void bindDeserializers() {
        bind(new TypeLiteral<IMessageDeserializer<IKVMessage, SerializedMessage>>() {})
                .to(new TypeLiteral<KVMessageDeserializer>() {});

        bind(new TypeLiteral<IMessageDeserializer<IKVAdminMessage, SerializedMessage>>() {})
                .to(new TypeLiteral<KVAdminMessageDeserializer>() {});

        bind(new TypeLiteral<IMessageDeserializer<IKVHeartbeatMessage, SerializedMessage>>() {})
                .to(new TypeLiteral<KVHeartbeatMessageDeserializer>() {});

        bind(new TypeLiteral<IDeserializer<NodeHealthInfos, String>>() {})
                .to(new TypeLiteral<NodeHealtInfosDeserializer>() {});

        bind(new TypeLiteral<IDeserializer<ServiceHealthInfos, String>>() {})
                .to(new TypeLiteral<ServiceHealthInfosDeserializer>() {});

        bind(new TypeLiteral<IDeserializer<ServerConnectionInfo, String>>() {})
                .to(new TypeLiteral<ServerConnectionInfoDeserializer>() {});
    }
}
