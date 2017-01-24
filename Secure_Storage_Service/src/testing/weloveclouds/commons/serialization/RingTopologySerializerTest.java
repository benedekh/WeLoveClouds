package testing.weloveclouds.commons.serialization;

import static org.mockito.Mockito.when;
import static weloveclouds.commons.status.ServiceStatus.HALTED;
import static weloveclouds.ecs.models.repository.NodeStatus.RUNNING;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.KVEcsNotificationMessageSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.RingTopologySerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.serialization.StorageNodeSerializer;
import weloveclouds.commons.serialization.deserialization.KVEcsNotificationMessageDeserializer;
import weloveclouds.commons.serialization.deserialization.NodeHealtInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.RingTopologyDeserializer;
import weloveclouds.commons.serialization.deserialization.ServiceHealthInfosDeserializer;
import weloveclouds.commons.serialization.deserialization.StorageNodeDeserializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.KVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * Unit tests to validate the {@link RingTopologySerializer<T>} correct behavior.
 * 
 * @author Benoit
 */
@RunWith(MockitoJUnitRunner.class)
public class RingTopologySerializerTest {
    private static final NodeStatus NODE_STATUS = RUNNING;

    private static final String REPLICA1_NAME = "JABBA";
    private static final String REPLICA1_IP = "127.0.0.3";
    private static final int REPLICA1_PORT = 5001;

    private static final String REPLICA2_NAME = "TROOPER";
    private static final String REPLICA2_IP = "127.0.0.4";
    private static final int REPLICA2_PORT = 5002;

    private static final String NODE_NAME = "VIENNA";
    private static final String NODE_IP_1 = "127.0.0.1";
    private static final int NODE_PORT_1 = 5000;
    private static final int SERVICE_PORT_1 = 5000;
    private static final int SERVICE_PRIORITY_1 = 2;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS_1 = 8;
    private static final String SERVICE_NAME_1 = "HAN SOLO";
    private static final String SERVICE_IP_1 = "127.0.0.1";
    private static final ServiceStatus SERVICE_STATUS_1 = ServiceStatus.RUNNING;

    private static final String NODE_NAME_2 = "MONTREAL";
    private static final String NODE_IP_2 = "127.0.0.2";
    private static final int NODE_PORT_2 = 5001;
    private static final int SERVICE_PORT_2 = 5001;
    private static final int SERVICE_PRIORITY_2 = 3;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS_2 = 9;
    private static final String SERVICE_NAME_2 = "LUKE SKYWALKER";
    private static final String SERVICE_IP_2 = "127.0.0.2";
    private static final ServiceStatus SERVICE_STATUS_2 = HALTED;

    private ServiceHealthInfosSerializer serviceHealthInfosSerializer;
    private HashRangeSerializer hashRangeSerializer;
    private HashSerializer hashSerializer;
    private ServerConnectionInfoSerializer serverConnectionInfoSerializer;
    private NodeHealthInfosSerializer nodeHealthInfosSerializer;
    private StorageNodeSerializer storageNodeSerializer;
    private RingTopologySerializer<StorageNode> ringTopologySerializer;
    private RingTopologyDeserializer<StorageNode> ringTopologyDeserializer;
    private StorageNode replica1;
    private StorageNode replica2;
    private HashRange storageNodeHashRange;
    private HashRange childHashRange1;
    private HashRange childHashRange2;
    private NodeHealthInfos nodeHealthInfos1;
    private NodeHealthInfos nodeHealthInfos2;
    private ServiceHealthInfos serviceHealthInfos1;
    private ServiceHealthInfos serviceHealthInfos2;
    private ServerConnectionInfo serviceEndpoint1;
    private ServerConnectionInfo serviceEndpoint2;
    private StorageNode storageNode1;
    private StorageNode storageNode2;

    @Mock
    private RingTopology<StorageNode> ringTopologyMock;

    @Before
    public void setUp() throws Exception {
        storageNodeHashRange =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        childHashRange1 = storageNodeHashRange;
        childHashRange2 = storageNodeHashRange;

        replica1 = new StorageNode.Builder().name(REPLICA1_NAME)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(REPLICA1_IP)
                        .port(REPLICA1_PORT).build())
                .build();

        replica2 = new StorageNode.Builder().name(REPLICA2_NAME)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(REPLICA2_IP)
                        .port(REPLICA2_PORT).build())
                .build();

        serviceEndpoint1 = new ServerConnectionInfo.Builder().ipAddress(SERVICE_IP_1)
                .port(SERVICE_PORT_1).build();
        serviceEndpoint2 = new ServerConnectionInfo.Builder().ipAddress(SERVICE_IP_2)
                .port(SERVICE_PORT_2).build();

        serviceHealthInfos1 = new ServiceHealthInfos.Builder().serviceName(SERVICE_NAME_1)
                .servicePriority(SERVICE_PRIORITY_1).serviceEnpoint(serviceEndpoint1)
                .serviceStatus(SERVICE_STATUS_1)
                .numberOfActiveConnections(NUMBER_OF_ACTIVE_CONNECTIONS_1).build();
        serviceHealthInfos2 = new ServiceHealthInfos.Builder().serviceName(SERVICE_NAME_2)
                .servicePriority(SERVICE_PRIORITY_2).serviceEnpoint(serviceEndpoint2)
                .serviceStatus(SERVICE_STATUS_2)
                .numberOfActiveConnections(NUMBER_OF_ACTIVE_CONNECTIONS_2).build();

        nodeHealthInfos1 = new NodeHealthInfos.Builder().nodeName(NODE_NAME).nodeStatus(NODE_STATUS)
                .addServiceHealtInfos(serviceHealthInfos1).build();

        nodeHealthInfos2 = new NodeHealthInfos.Builder().nodeName(NODE_NAME).nodeStatus(NODE_STATUS)
                .addServiceHealtInfos(serviceHealthInfos2).build();

        storageNode1 = new StorageNode.Builder().name(NODE_NAME)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(NODE_IP_1)
                        .port(NODE_PORT_1).build())
                .hashRange(storageNodeHashRange)
                .readRanges(Arrays.asList(childHashRange1, childHashRange2))
                .healthInfos(nodeHealthInfos1).replicas(Arrays.asList(replica1, replica2)).build();

        storageNode2 = new StorageNode.Builder().name(NODE_NAME_2)
                .serverConnectionInfo(new ServerConnectionInfo.Builder().ipAddress(NODE_IP_2)
                        .port(NODE_PORT_2).build())
                .hashRange(storageNodeHashRange)
                .readRanges(Arrays.asList(childHashRange1, childHashRange2))
                .healthInfos(nodeHealthInfos2).replicas(Arrays.asList(replica1, replica2)).build();

        serverConnectionInfoSerializer = new ServerConnectionInfoSerializer();
        serviceHealthInfosSerializer =
                new ServiceHealthInfosSerializer(serverConnectionInfoSerializer);
        nodeHealthInfosSerializer = new NodeHealthInfosSerializer(serviceHealthInfosSerializer);
        hashRangeSerializer = new HashRangeSerializer();
        hashSerializer = new HashSerializer();
        storageNodeSerializer = new StorageNodeSerializer(serverConnectionInfoSerializer,
                hashSerializer, hashRangeSerializer, nodeHealthInfosSerializer);
        ringTopologySerializer = new RingTopologySerializer<>(storageNodeSerializer);
        ringTopologyDeserializer = new RingTopologyDeserializer<>(new StorageNodeDeserializer(
                new ServerConnectionInfoDeserializer(), new HashRangeDeserializer(),
                new NodeHealtInfosDeserializer(new ServiceHealthInfosDeserializer(
                        new ServerConnectionInfoDeserializer()))));
    }

    @Test
    public void shouldSerializeValidRingTopology() throws Exception {
        when(ringTopologyMock.getNodes()).thenReturn(Arrays.asList(storageNode1, storageNode2));
        AbstractXMLNode serializedRingTopology = ringTopologySerializer.serialize(ringTopologyMock);
        RingTopology<StorageNode> deserializeRingTopology =
                ringTopologyDeserializer.deserialize(serializedRingTopology.toString());
        SerializedMessage serializedMessage =
                new KVEcsNotificationMessageSerializer(ringTopologySerializer,
                        nodeHealthInfosSerializer)
                                .serialize(new KVEcsNotificationMessage.Builder()
                                        .status(IKVEcsNotificationMessage.Status.TOPOLOGY_UPDATE)
                                        .ringTopology(ringTopologyMock).build());
        IKVEcsNotificationMessage deserializedMessage = new KVEcsNotificationMessageDeserializer(
                new RingTopologyDeserializer<>(new StorageNodeDeserializer(
                        new ServerConnectionInfoDeserializer(), new HashRangeDeserializer(),
                        new NodeHealtInfosDeserializer(new ServiceHealthInfosDeserializer(
                                new ServerConnectionInfoDeserializer())))),
                new NodeHealtInfosDeserializer(
                        new ServiceHealthInfosDeserializer(new ServerConnectionInfoDeserializer())))
                                .deserialize(serializedMessage);
        System.out.println(new String(serializedMessage.getBytes()));
    }
}
