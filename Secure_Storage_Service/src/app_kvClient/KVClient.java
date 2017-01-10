package app_kvClient;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.commands.CommandFactory;
import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.client.core.Client;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.configuration.models.KVServerPortConstants;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.commons.utils.LogSetup;

/**
 * Client application. See {@link Client} for more details.
 *
 * @author Benoit, Benedek, Hunton
 */
public class KVClient {

    private static final String DEFAULT_LOG_PATH = "logs/client.log";
    private static final String DEFAULT_LOG_LEVEL = "ALL";

    private static final int CLI_CLIENT_NAME_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(KVClient.class);

    public static String clientName = "client";

    /**
     * The entry point of the application.
     *
     * @param args is discarded so far
     */
    public static void main(String[] args) {
        initializeLoggerWithLevel(DEFAULT_LOG_LEVEL);
        ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);
        try {
            ArgumentsValidator.validateCLIArgumentsForClientStart(args);
            clientName = args[CLI_CLIENT_NAME_INDEX];

            ServerConnectionInfo bootstrapConnectionInfo =
                    new ServerConnectionInfo.Builder().ipAddress("weloveclouds-lb.com")
                            .port(10000/*KVServerPortConstants.KVCLIENT_REQUESTS_PORT*/).build();
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
            LOGGER.error("Unable to resolve default, bootstrap server IP address.");
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Initializes the root logger with the referred logLevel.
     */
    private static void initializeLoggerWithLevel(String logLevel) {
        initializeLoggerWithLevel(Level.toLevel(logLevel));
    }

    /**
     * Initializes the root logger with the referred logLevel.
     */
    private static void initializeLoggerWithLevel(Level logLevel) {
        try {
            new LogSetup(DEFAULT_LOG_PATH, logLevel);
        } catch (IOException ex) {
            System.err.println(StringUtils.join(" ", "Log file cannot be created on path",
                    DEFAULT_LOG_PATH, "due to an error:", ex.getMessage()));
        }
    }
}
