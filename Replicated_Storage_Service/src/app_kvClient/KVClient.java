package app_kvClient;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.core.Client;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.models.configuration.KVServerPortConstants;
import weloveclouds.server.utils.LogSetup;

/**
 * Client application. See {@link Client} for more details.
 *
 * @author Benoit, Benedek, Hunton
 */
public class KVClient {
    private static final Logger LOGGER = Logger.getLogger(KVClient.class);

    /**
     * The entry point of the application.
     *
     * @param args is discarded so far
     */
    public static void main(String[] args) {
        String logFile = "logs/client.log";
        try {
            new LogSetup(logFile, Level.OFF);

            ServerConnectionInfo bootstrapConnectionInfo =
                    new ServerConnectionInfo.Builder().ipAddress("localhost")
                            .port(KVServerPortConstants.KVCLIENT_REQUESTS_PORT).build();
            IKVCommunicationApiV2 serverCommunication = new KVCommunicationApiFactory()
                    .createKVCommunicationApiV2(bootstrapConnectionInfo);

            try {
                serverCommunication.connect();
            } catch (Exception ex) {
                LOGGER.error("Unable to connect to the default, bootstrap server.");
            }

            RingMetadataDeserializer ringMetadataDeserializer = new RingMetadataDeserializer();
            CommandFactory commandFactory =
                    new CommandFactory(serverCommunication, ringMetadataDeserializer);

            Client client = new Client(System.in, commandFactory);
            client.run();
        } catch (IOException ex) {
            System.err.println(CustomStringJoiner.join(" ", "Log file cannot be created on path ",
                    logFile, "due to an error:", ex.getMessage()));
        }
    }

}
