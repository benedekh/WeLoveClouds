package weloveclouds.evaluation.dataloading.connection;

import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.helper.RingMetadataDeserializer;

public class ClientConnectionFactory {

    private static final String DEFAULT_IP_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 50000;

    private static final Logger LOGGER = LogManager.getLogger(ClientConnectionFactory.class);

    public static ClientConnection createDefaultClient() {
        try {
            ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                    .ipAddress(DEFAULT_IP_ADDRESS).port(DEFAULT_PORT).build();
            IKVCommunicationApiV2 serverCommunication = new CommunicationApiFactory()
                    .createKVCommunicationApiV2(bootstrapConnectionInfo);

            RingMetadataDeserializer ringMetadataDeserializer = new RingMetadataDeserializer();

            return new ClientConnection(serverCommunication, ringMetadataDeserializer);
        } catch (UnknownHostException e) {
            LOGGER.error(e);
        }
        return null;

    }

}
