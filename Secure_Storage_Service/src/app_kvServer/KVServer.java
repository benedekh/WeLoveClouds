package app_kvServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.KVHeartbeatMessageSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.commons.utils.LogSetup;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.server.client.commands.ServerCommandFactory;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerPortConstants;
import weloveclouds.server.configuration.models.KVServerPortContext;
import weloveclouds.server.core.ServerCLIHandler;
import weloveclouds.server.core.ServerFactory;
import weloveclouds.server.monitoring.heartbeat.DefaultHearbeatSenderService;
import weloveclouds.server.monitoring.heartbeat.HeartbeatSenderService;
import weloveclouds.server.monitoring.heartbeat.NodeHealthMonitor;
import weloveclouds.server.services.datastore.DataAccessServiceFactory;
import weloveclouds.server.services.datastore.IDataAccessService;
import weloveclouds.server.services.datastore.IReplicableDataAccessService;
import weloveclouds.server.services.datastore.models.DataAccessServiceInitializationContext;
import weloveclouds.server.services.replication.ReplicationService;
import weloveclouds.server.services.replication.ReplicationServiceFactory;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;

/**
 * KVServer application which starts a {@link ServerSocket} to accept connections and to transform
 * them into requests to the {@link IDataAccessService}.
 *
 * @author Benoit, Benedek, Hunton
 */
public class KVServer {

    protected static final Path PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER = Paths.get("./");

    private static final String DEFAULT_LOG_PATH = "logs/server.log";
    private static final String DEFAULT_LOG_LEVEL = "DEBUG";
	/* Not deleting this just yet
    private static final String DEFAULT_LOG_LEVEL = "ALL";

    private static final Path PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER = Paths.get("./");

    private static final int CLI_KVCLIENT_PORT_INDEX = 0;
    private static final int CLI_KVSERVER_PORT_INDEX = 1;
    private static final int CLI_KVECS_PORT_INDEX = 2;
    private static final int CLI_CACHE_SIZE_INDEX = 3;
    private static final int CLI_DISPLACEMENT_STRATEGY_INDEX = 4;
    private static final int CLI_LOG_LEVEL_INDEX = 5;
    private static final int CLI_SERVER_NAME_INDEX = 6;
    private static final int CLI_LOADBALANCER_IP_INDEX = 7;
    private static final int CLI_LOADBALANCER_PORT_INDEX = 8;
	*/
    private static final Logger LOGGER = Logger.getLogger(KVServer.class);

    private static LogSetup logSetup = null;

    /**
     * The entry point of the application.
     */
    public static void main(String[] args) {
        ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);
        initializeLoggerWithLevel(DEFAULT_LOG_LEVEL);

        if (args.length == 0) {
            startInteractiveCLIMode();
        } else {
            startNonInteractiveMode(args);
        }
    }

    /**
     * Start the service with a non-interactive mode, directly from the command-line.
     */
    private static void startNonInteractiveMode(String[] cliArguments) {
        try {
            KVServerCLIArgsRegistry cliRegistry = KVServerCLIArgsRegistry.getInstance();
            cliRegistry.initializeArguments(cliArguments);
            createAndStartServers(cliRegistry.getPortConfigurationContext(),
                    cliRegistry.getReplicableDAS(), cliRegistry.getLoadbalancerConnectionInfo());
        } catch (Throwable ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Creates and start all three servers for the different requests.
     * 
     * @param portConfigurationContext context object which contains the port configuration to serve
     *        the different requests (KVClient, KVServer, KVECS)
     * @param dataAccessService the data access service
     * @param loadBalancerInfo connection information to the loadbalancer
     * 
     * @throws IOException if an error occurs
     */
    private static void createAndStartServers(KVServerPortContext portConfigurationContext,
            IReplicableDataAccessService dataAccessService, ServerConnectionInfo loadBalancerInfo)
            throws IOException {
        ServerFactory serverFactory = new ServerFactory();

        HeartbeatSenderService heartbeatSender = null;
        if (loadBalancerInfo != null) {
            heartbeatSender = new HeartbeatSenderService.Builder()
                    .communicationApi(new CommunicationApiFactory().createCommunicationApiV1())
                    .loadbalancerConnectionInfo(loadBalancerInfo)
                    .heartbeatSerializer(new KVHeartbeatMessageSerializer(
                            new NodeHealthInfosSerializer(new ServiceHealthInfosSerializer(
                                    new ServerConnectionInfoSerializer()))))
                    .build();
        } else {
            heartbeatSender = new DefaultHearbeatSenderService.Builder().build();
        }

        NodeHealthMonitor.Builder nodeHealthMonitorBuilder =
                new NodeHealthMonitor.Builder().hearbeatSenderService(heartbeatSender)
                        .nodeHealthInfosBuilder(new NodeHealthInfos.Builder()
                                .nodeName(KVServerCLIArgsRegistry.getInstance().getServerName()));

        weloveclouds.server.core.KVServer kvServer = new weloveclouds.server.core.KVServer.Builder()
                .serverFactory(serverFactory).portConfiguration(portConfigurationContext)
                .dataAccessService(dataAccessService)
                .nodeHealthMonitorBuilder(nodeHealthMonitorBuilder).build();
        kvServer.start();
    }

    /**
     * Starts the service with interactive command-line mode.
     */
    private static void startInteractiveCLIMode() {
        initializeLoggerWithLevel(Level.OFF);
        ServerCLIHandler cli = new ServerCLIHandler(System.in,
                new ServerCommandFactory(new DataAccessServiceFactory(), new ServerFactory()));
        cli.run();
    }

    /**
     * Initializes the root logger with the referred log level.
     */
    protected static void initializeLoggerWithLevel(String logLevel) {
        initializeLoggerWithLevel(Level.toLevel(logLevel));
    }

    /**
     * Initializes the root logger with the referred log level.
     */
    private static void initializeLoggerWithLevel(Level logLevel) {
        try {
            if (logSetup == null) {
                logSetup = new LogSetup(DEFAULT_LOG_PATH, logLevel);
            } else {
                Logger.getRootLogger().setLevel(logLevel);
            }
        } catch (IOException ex) {
            System.err.println(StringUtils.join(" ", "Log file cannot be created on path",
                    DEFAULT_LOG_PATH, "due to an error:", ex.getMessage()));
        }
    }

    /**
     * Start KV Server at given port. ONLY FOR TESTING PURPOSES!!!
     *
     * @param port given port for storage server to operate
     * @param cacheSize specifies how many key-value pairs the server is allowed to keep in-memory
     * @param strategy specifies the displacement strategy in the cache, in case the cache is full
     *        and there is a GET or PUT request on a key that is currently not contained in the
     *        cache. Options are "FIFO", "LRU", and "LFU".
     */
    public KVServer(int port, int cacheSize, String strategy) {
        Path defaultStoragePath = Paths.get("logs/testing/");
        if (!defaultStoragePath.toAbsolutePath().toFile().exists()) {
            defaultStoragePath.toAbsolutePath().toFile().mkdirs();
        }

        DisplacementStrategy displacementStrategy =
                StrategyFactory.createDisplacementStrategy(strategy);

        if (displacementStrategy == null) {
            throw new IllegalArgumentException(
                    "Invalid strategy. Valid values are: FIFO, LRU, LFU");
        }

        ArgumentsValidator.validatePortArguments(new String[] {String.valueOf(port)});

        DataAccessServiceInitializationContext initializationContext =
                new DataAccessServiceInitializationContext.Builder().cacheSize(cacheSize)
                        .displacementStrategy(displacementStrategy)
                        .rootFolderPath(defaultStoragePath).build();

        ReplicationServiceFactory replicationServiceFactory = new ReplicationServiceFactory();
        ReplicationService replicationService =
                replicationServiceFactory.createReplicationServiceWith2PC();

        IReplicableDataAccessService dataAccessService =
                new DataAccessServiceFactory().createInitializedReplicableDataAccessService(
                        initializationContext, replicationService);
        try {
            KVServerPortContext portConfigurationContext = new KVServerPortContext.Builder()
                    .clientPort(port).serverPort(KVServerPortConstants.KVSERVER_REQUESTS_PORT)
                    .ecsPort(KVServerPortConstants.KVECS_REQUESTS_PORT).build();
            createAndStartServers(portConfigurationContext, dataAccessService, null);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}
