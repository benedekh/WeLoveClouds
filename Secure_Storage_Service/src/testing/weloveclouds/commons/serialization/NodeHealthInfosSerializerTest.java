package testing.weloveclouds.commons.serialization;

import static weloveclouds.commons.status.ServiceStatus.HALTED;
import static weloveclouds.commons.status.ServiceStatus.INITIALIZED;
import static weloveclouds.commons.status.ServiceStatus.RUNNING;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * Unit tests to validate the {@link NodeHealthInfosSerializer} correct behavior.
 * 
 * @author Benoit
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeHealthInfosSerializerTest {
    private static final String NODE_NAME = "C3PO";
    private static final NodeStatus NODE_STATUS = NodeStatus.RUNNING;

    private static final int SERVICE_PORT_1 = 5000;
    private static final int SERVICE_PRIORITY_1 = 2;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS_1 = 8;
    private static final String SERVICE_NAME_1 = "HAN SOLO";
    private static final String SERVICE_IP_1 = "127.0.0.1";
    private static final ServiceStatus SERVICE_STATUS_1 = RUNNING;

    private static final int SERVICE_PORT_2 = 5001;
    private static final int SERVICE_PRIORITY_2 = 3;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS_2 = 9;
    private static final String SERVICE_NAME_2 = "LUKE SKYWALKER";
    private static final String SERVICE_IP_2 = "127.0.0.2";
    private static final ServiceStatus SERVICE_STATUS_2 = HALTED;

    private static final int SERVICE_PORT_3 = 5002;
    private static final int SERVICE_PRIORITY_3 = 4;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS_3 = 10;
    private static final String SERVICE_NAME_3 = "DARTH VADER";
    private static final String SERVICE_IP_3 = "127.0.0.3";
    private static final ServiceStatus SERVICE_STATUS_3 = INITIALIZED;

    private ISerializer<AbstractXMLNode, NodeHealthInfos> nodeHealthInfosSerializer;
    private ISerializer<AbstractXMLNode, ServiceHealthInfos> serviceHealthInfosSerializer;
    private ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoSerializer;
    private NodeHealthInfos nodeHealthInfos;
    private ServiceHealthInfos serviceHealthInfos_1;
    private ServiceHealthInfos serviceHealthInfos_2;
    private ServiceHealthInfos serviceHealthInfos_3;
    private ServerConnectionInfo serviceEndpoint_1;
    private ServerConnectionInfo serviceEndpoint_2;
    private ServerConnectionInfo serviceEndpoint_3;

    @Before
    public void setUp() throws Exception {
        serverConnectionInfoSerializer = new ServerConnectionInfoSerializer();
        serviceHealthInfosSerializer =
                new ServiceHealthInfosSerializer(serverConnectionInfoSerializer);
        nodeHealthInfosSerializer = new NodeHealthInfosSerializer(serviceHealthInfosSerializer);
        serviceEndpoint_1 = new ServerConnectionInfo.Builder().ipAddress(SERVICE_IP_1)
                .port(SERVICE_PORT_1).build();
        serviceEndpoint_2 = new ServerConnectionInfo.Builder().ipAddress(SERVICE_IP_2)
                .port(SERVICE_PORT_2).build();
        serviceEndpoint_3 = new ServerConnectionInfo.Builder().ipAddress(SERVICE_IP_3)
                .port(SERVICE_PORT_3).build();

        serviceHealthInfos_1 = new ServiceHealthInfos.Builder().serviceName(SERVICE_NAME_1)
                .servicePriority(SERVICE_PRIORITY_1).serviceEnpoint(serviceEndpoint_1)
                .serviceStatus(SERVICE_STATUS_1)
                .numberOfActiveConnections(NUMBER_OF_ACTIVE_CONNECTIONS_1).build();
        serviceHealthInfos_2 = new ServiceHealthInfos.Builder().serviceName(SERVICE_NAME_2)
                .servicePriority(SERVICE_PRIORITY_2).serviceEnpoint(serviceEndpoint_2)
                .serviceStatus(SERVICE_STATUS_2)
                .numberOfActiveConnections(NUMBER_OF_ACTIVE_CONNECTIONS_2).build();
        serviceHealthInfos_3 = new ServiceHealthInfos.Builder().serviceName(SERVICE_NAME_3)
                .servicePriority(SERVICE_PRIORITY_3).serviceEnpoint(serviceEndpoint_3)
                .serviceStatus(SERVICE_STATUS_3)
                .numberOfActiveConnections(NUMBER_OF_ACTIVE_CONNECTIONS_3).build();

        nodeHealthInfos = new NodeHealthInfos.Builder().nodeName(NODE_NAME).nodeStatus(NODE_STATUS)
                .addServiceHealtInfos(serviceHealthInfos_1)
                .addServiceHealtInfos(serviceHealthInfos_2)
                .addServiceHealtInfos(serviceHealthInfos_3).build();
    }

    @Test
    public void shouldSerializeValidNodeHealtInfos() throws Exception {
        String serializedNodeHealtInfo =
                nodeHealthInfosSerializer.serialize(nodeHealthInfos).toString();
    }
}
