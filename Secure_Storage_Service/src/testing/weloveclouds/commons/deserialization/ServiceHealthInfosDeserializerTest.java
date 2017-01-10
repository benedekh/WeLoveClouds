package testing.weloveclouds.commons.deserialization;

import static org.fest.assertions.Assertions.assertThat;
import static weloveclouds.commons.status.ServiceStatus.RUNNING;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.deserialization.ServiceHealthInfosDeserializer;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * Unit tests to validate the {@link ServiceHealthInfosDeserializer} correct behavior.
 * 
 * @author Benoit
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceHealthInfosDeserializerTest {
    private static final int SERVICE_PORT = 5000;
    private static final int SERVICE_PRIORITY = 2;
    private static final int NUMBER_OF_ACTIVE_CONNECTIONS = 8;
    private static final String SERVICE_NAME = "AN SOLO";
    private static final String SERVICE_IP = "127.0.0.1";
    private static final ServiceStatus SERVICE_STATUS = RUNNING;

    private IDeserializer<ServiceHealthInfos, String> serviceHealthInfosDeserializer;
    private IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer;

    @Before
    public void setUp() {
        serverConnectionInfoDeserializer = new ServerConnectionInfoDeserializer();
        serviceHealthInfosDeserializer =
                new ServiceHealthInfosDeserializer(serverConnectionInfoDeserializer);
    }

    @Test
    public void shouldDeserializeValidServiceHealthInfos() throws Exception {
        String serializedServiceHealthInfos = StringUtils.join("", "<SERVICE>",
                "<NAME>", SERVICE_NAME, "</NAME>",
                "<STATUS>", SERVICE_STATUS.name(), "</STATUS>",
                "<CONNECTION_INFO>",
                "<IP_ADDRESS>", SERVICE_IP, "</IP_ADDRESS>",
                "<PORT>", String.valueOf(SERVICE_PORT), "</PORT>", "</CONNECTION_INFO>",
                "<ACTIVE_CONNECTIONS>", String.valueOf(NUMBER_OF_ACTIVE_CONNECTIONS),
                "</ACTIVE_CONNECTIONS>",
                "<PRIORITY>", String.valueOf(SERVICE_PRIORITY), "</PRIORITY>",
                "</SERVICE>");
        ServiceHealthInfos deserializedServiceHealthInfos = serviceHealthInfosDeserializer
                .deserialize(serializedServiceHealthInfos);

        assertThat(deserializedServiceHealthInfos.getServiceName()).isEqualTo(SERVICE_NAME);
        assertThat(deserializedServiceHealthInfos.getNumberOfActiveConnections()).isEqualTo
                (NUMBER_OF_ACTIVE_CONNECTIONS);
        assertThat(deserializedServiceHealthInfos.getServicePriority()).isEqualTo(SERVICE_PRIORITY);
        assertThat(deserializedServiceHealthInfos.getServiceStatus()).isEqualTo(RUNNING);
        assertThat(deserializedServiceHealthInfos.getServiceEndpoint().getIpAddress().toString())
                .isEqualTo(StringUtils.join("", "/", SERVICE_IP));
        assertThat(deserializedServiceHealthInfos.getServiceEndpoint().getPort()).isEqualTo
                (SERVICE_PORT);
    }
}
