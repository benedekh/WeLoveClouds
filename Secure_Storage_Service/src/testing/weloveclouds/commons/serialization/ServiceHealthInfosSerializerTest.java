package testing.weloveclouds.commons.serialization;

import static org.fest.assertions.Assertions.assertThat;
import static weloveclouds.commons.status.ServiceStatus.RUNNING;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * Unit tests to validate the {@link ServiceHealthInfosSerializer} correct behavior.
 * 
 * @author Benoit
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceHealthInfosSerializerTest {
    private static final int SERVICE_PORT = 5000;
    private static final int SERVICE_PRIORITY = 2;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS = 8;
    private static final String SERVICE_NAME = "AN SOLO";
    private static final String SERVICE_IP = "127.0.0.1";
    private static final ServiceStatus SERVICE_STATUS = RUNNING;

    private ISerializer<AbstractXMLNode, ServiceHealthInfos> serviceHealthInfosSerializer;
    private ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoSerializer;
    private ServerConnectionInfo serviceEndpoint;
    private ServiceHealthInfos serviceHealthInfos;

    @Before
    public void setUp() throws Exception {
        serverConnectionInfoSerializer = new ServerConnectionInfoSerializer();
        serviceHealthInfosSerializer = new ServiceHealthInfosSerializer
                (serverConnectionInfoSerializer);
        serviceEndpoint = new ServerConnectionInfo.Builder()
                .ipAddress(SERVICE_IP)
                .port(SERVICE_PORT)
                .build();
        serviceHealthInfos = new ServiceHealthInfos.Builder()
                .serviceName(SERVICE_NAME)
                .serviceStatus(SERVICE_STATUS)
                .serviceEnpoint(serviceEndpoint)
                .servicePriority(SERVICE_PRIORITY)
                .numberOfActiveConnections(NUMBER_OF_ACTIVE_CONNECTIONS)
                .build();
    }

    @Test
    public void shouldSerializeValidServiceHealthInfos() {
        String expectedResult = StringUtils.join("", "<SERVICE>",
                "<NAME>", SERVICE_NAME, "</NAME>",
                "<STATUS>", SERVICE_STATUS.name(), "</STATUS>",
                "<CONNECTION_INFO>",
                "<IP_ADDRESS>", SERVICE_IP, "</IP_ADDRESS>",
                "<PORT>", String.valueOf(SERVICE_PORT), "</PORT>", "</CONNECTION_INFO>",
                "<ACTIVE_CONNECTIONS>", String.valueOf(NUMBER_OF_ACTIVE_CONNECTIONS),
                "</ACTIVE_CONNECTIONS>",
                "<PRIORITY>", String.valueOf(SERVICE_PRIORITY), "</PRIORITY>",
                "</SERVICE>");
        String serializedServiceHealthInfos = serviceHealthInfosSerializer.serialize
                (serviceHealthInfos).toString();

        assertThat(serializedServiceHealthInfos).isEqualTo(expectedResult);
    }
}
