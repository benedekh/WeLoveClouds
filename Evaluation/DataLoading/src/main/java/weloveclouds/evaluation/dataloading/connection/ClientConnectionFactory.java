package weloveclouds.evaluation.dataloading.connection;

import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;

/**
 * The type Client connection factory.
 * 
 * @author Benedek
 */
public class ClientConnectionFactory {

    private static final Logger LOGGER = LogManager.getLogger(ClientConnectionFactory.class);

    /**
     * Create default client client connection.
     *
     * @return the client connection
     */
    public static ClientConnection createDefaultClient(String serverIp, int serverPort) {
        try {
            ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                    .ipAddress(serverIp).port(serverPort).build();
            IKVCommunicationApiV2 serverCommunication = new KVCommunicationApiFactory()
                    .createKVCommunicationApiV2(bootstrapConnectionInfo);

            RingMetadataDeserializer ringMetadataDeserializer = new RingMetadataDeserializer();

            return new ClientConnection(serverCommunication, ringMetadataDeserializer);
        } catch (UnknownHostException e) {
            LOGGER.error(e);
        }
        return null;

    }

}
