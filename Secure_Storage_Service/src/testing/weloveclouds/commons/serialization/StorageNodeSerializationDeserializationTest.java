package testing.weloveclouds.commons.serialization;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.serialization.StorageNodeSerializer;
import weloveclouds.commons.serialization.deserialization.NodeHealtInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.ServiceHealthInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.StorageNodeDeserializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * Unit tests to validate different scenarios for storage node health info serialization and
 * deserialization.
 * 
 * @author Benoit
 */
public class StorageNodeSerializationDeserializationTest {
    private static final String STORAGE_NODE_NAME = "R2D2";
    private static final String STORAGE_NODE_IP = "127.0.0.1";
    private static final int STORAGE_NODE_PORT = 5000;

    private static final String REPLICA1_NAME = "JABBA";
    private static final String REPLICA1_IP = "127.0.0.2";
    private static final int REPLICA1_PORT = 5001;

    private static final String REPLICA2_NAME = "TROOPER";
    private static final String REPLICA2_IP = "127.0.0.3";
    private static final int REPLICA2_PORT = 5002;

    private ISerializer<AbstractXMLNode, ServiceHealthInfos> serviceHealthInfosSerializer;
    private ISerializer<AbstractXMLNode, NodeHealthInfos> nodeNodeHealthInfosSerializer;
    private ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer;
    private ISerializer<String, Hash> hashSerializer;
    private ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoSerializer;
    private ISerializer<AbstractXMLNode, StorageNode> storageNodeSerializer;
    private IDeserializer<StorageNode, String> storageNodeDeserializer;
    private IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer;
    private IDeserializer<HashRange, String> hashRangeDeserializer;
    private IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer;
    private IDeserializer<ServiceHealthInfos, String> serviceHealthInfosDeserializer;
    private StorageNode storageNode;
    private StorageNode replica1;
    private StorageNode replica2;
    private HashRange storageNodeHashRange;
    private HashRange childHashRange1;
    private HashRange childHashRange2;
    private NodeHealthInfos nodeHealthInfos;

    @Before
    public void setUp() throws Exception {
        hashRangeSerializer = new HashRangeSerializer();
        hashSerializer = new HashSerializer();
        serverConnectionInfoSerializer = new ServerConnectionInfoSerializer();
        serviceHealthInfosSerializer =
                new ServiceHealthInfosSerializer(serverConnectionInfoSerializer);
        nodeNodeHealthInfosSerializer = new NodeHealthInfosSerializer(serviceHealthInfosSerializer);
        storageNodeSerializer = new StorageNodeSerializer(serverConnectionInfoSerializer,
                hashSerializer, hashRangeSerializer, nodeNodeHealthInfosSerializer);

        serverConnectionInfoDeserializer = new ServerConnectionInfoDeserializer();
        hashRangeDeserializer = new HashRangeDeserializer();
        serviceHealthInfosDeserializer =
                new ServiceHealthInfosDeserializer(serverConnectionInfoDeserializer);
        nodeHealthInfosDeserializer =
                new NodeHealtInfosDeserializer(serviceHealthInfosDeserializer);
        storageNodeDeserializer = new StorageNodeDeserializer(serverConnectionInfoDeserializer,
                hashRangeDeserializer, nodeHealthInfosDeserializer);

        storageNodeHashRange =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        childHashRange1 = storageNodeHashRange;
        childHashRange2 = storageNodeHashRange;
        nodeHealthInfos = new NodeHealthInfos.Builder().nodeName(STORAGE_NODE_NAME)
                .nodeStatus(NodeStatus.RUNNING).build();

        replica1 = new StorageNode.Builder().name(REPLICA1_NAME)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(REPLICA1_IP)
                        .port(REPLICA1_PORT).build())
                .build();

        replica2 = new StorageNode.Builder().name(REPLICA2_NAME)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(REPLICA2_IP)
                        .port(REPLICA2_PORT).build())
                .build();

        storageNode = new StorageNode.Builder().name(STORAGE_NODE_NAME)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(STORAGE_NODE_IP)
                        .port(STORAGE_NODE_PORT).build())
                .hashRange(storageNodeHashRange)
                .readRanges(Arrays.asList(childHashRange1, childHashRange2))
                .healthInfos(nodeHealthInfos).replicas(Arrays.asList(replica1, replica2)).build();
    }

    @Test
    public void shouldSerializeValidStorageNode() throws Exception {
        String serializedStorageNode = storageNodeSerializer.serialize(storageNode).toString();
        StorageNode deserializedStorageNode =
                storageNodeDeserializer.deserialize(serializedStorageNode);
        assertThat(deserializedStorageNode.getName()).isEqualTo(STORAGE_NODE_NAME);
        assertThat(deserializedStorageNode.getPort()).isEqualTo(STORAGE_NODE_PORT);
        assertThat(deserializedStorageNode.getHashRange()).isEqualTo(storageNodeHashRange);
        assertThat(deserializedStorageNode.getReplicas().size()).isEqualTo(2);
        assertThat(deserializedStorageNode.getReadRanges().size()).isEqualTo(2);
        assertThat(deserializedStorageNode.getReadRanges()).containsOnly(childHashRange1,
                childHashRange2);
        assertThat(deserializedStorageNode.getIpAddress()).isEqualTo("/" + STORAGE_NODE_IP);
    }
}
