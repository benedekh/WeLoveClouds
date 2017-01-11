package weloveclouds.commons.serialization.configuration.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.KVEcsNotificationMessageSerializer;
import weloveclouds.commons.serialization.KVHeartbeatMessageSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.RingTopologySerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.serialization.StorageNodeSerializer;
import weloveclouds.commons.serialization.deserialization.KVEcsNotificationMessageDeserializer;
import weloveclouds.commons.serialization.deserialization.KVHeartbeatMessageDeserializer;
import weloveclouds.commons.serialization.deserialization.NodeHealtInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.RingTopologyDeserializer;
import weloveclouds.commons.serialization.deserialization.ServiceHealthInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.StorageNodeDeserializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
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

        bind(new TypeLiteral<IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage>>() {})
                .to(new TypeLiteral<KVEcsNotificationMessageSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, NodeHealthInfos>>() {})
                .to(new TypeLiteral<NodeHealthInfosSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, ServiceHealthInfos>>() {})
                .to(new TypeLiteral<ServiceHealthInfosSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, ServerConnectionInfo>>() {})
                .to(new TypeLiteral<ServerConnectionInfoSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, RingTopology<StorageNode>>>() {})
                .to(new TypeLiteral<RingTopologySerializer<StorageNode>>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, StorageNode>>() {})
                .to(new TypeLiteral<StorageNodeSerializer>() {});

        bind(new TypeLiteral<ISerializer<AbstractXMLNode, HashRange>>() {})
                .to(new TypeLiteral<HashRangeSerializer>() {});

        bind(new TypeLiteral<ISerializer<String, Hash>>() {})
                .to(new TypeLiteral<HashSerializer>() {});
    }

    private void bindDeserializers() {
        bind(new TypeLiteral<IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage>>() {})
                .to(new TypeLiteral<KVEcsNotificationMessageDeserializer>() {});

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

        bind(new TypeLiteral<IDeserializer<RingTopology<StorageNode>, String>>() {})
                .to(new TypeLiteral<RingTopologyDeserializer<StorageNode>>() {});

        bind(new TypeLiteral<IDeserializer<StorageNode, String>>() {})
                .to(new TypeLiteral<StorageNodeDeserializer>() {});

        bind(new TypeLiteral<IDeserializer<HashRange, String>>() {})
                .to(new TypeLiteral<HashRangeDeserializer>() {});
    }
}
