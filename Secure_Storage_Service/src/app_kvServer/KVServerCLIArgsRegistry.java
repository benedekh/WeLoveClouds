package app_kvServer;

import java.net.UnknownHostException;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerPortContext;
import weloveclouds.server.services.datastore.DataAccessServiceFactory;
import weloveclouds.server.services.datastore.IReplicableDataAccessService;
import weloveclouds.server.services.datastore.models.DataAccessServiceInitializationContext;
import weloveclouds.server.services.replication.ReplicationService;
import weloveclouds.server.services.replication.ReplicationServiceFactory;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.StrategyFactory;

/**
 * Command-line argument registry for {@link KVServer}.
 * 
 * @author Benedek
 */
public class KVServerCLIArgsRegistry {

    private static final KVServerCLIArgsRegistry INSTANCE = new KVServerCLIArgsRegistry();

    private static final int CLI_KVCLIENT_PORT_INDEX = 0;
    private static final int CLI_KVSERVER_PORT_INDEX = 1;
    private static final int CLI_KVECS_PORT_INDEX = 2;
    private static final int CLI_CACHE_SIZE_INDEX = 3;
    private static final int CLI_DISPLACEMENT_STRATEGY_INDEX = 4;
    private static final int CLI_LOG_LEVEL_INDEX = 5;
    private static final int CLI_SERVER_NAME_INDEX = 6;
    private static final int CLI_LOADBALANCER_IP_INDEX = 7;
    private static final int CLI_LOADBALANCER_PORT_INDEX = 8;

    private String serverName = "server";
    private int cacheSize;
    private DisplacementStrategy displacementStrategy;

    private int kvClientPort;
    private int kvServerPort;
    private int kvECSPort;

    private String loadbalancerIp;
    private int loadbalancerPort;

    private KVServerCLIArgsRegistry() {

    }

    public static KVServerCLIArgsRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the argument registry based on the command-line arguments received upon start.
     */
    public void initializeArguments(String[] cliArguments) {
        if (cliArguments == null
                || cliArguments.length <= ArgumentsValidator.REQUIRED_CLI_ARGUMENT_NUMBER_WITHOUT_LOADBALANCER) {
            ArgumentsValidator.validateCLIArgumentsForServerStart(cliArguments);
        } else {
            ArgumentsValidator.validateCLIArgumentsWithLoadbalancerForServerStart(cliArguments);
        }

        KVServer.initializeLoggerWithLevel(cliArguments[CLI_LOG_LEVEL_INDEX]);
        serverName = cliArguments[CLI_SERVER_NAME_INDEX];

        cacheSize = Integer.valueOf(cliArguments[CLI_CACHE_SIZE_INDEX]);
        displacementStrategy = StrategyFactory
                .createDisplacementStrategy(cliArguments[CLI_DISPLACEMENT_STRATEGY_INDEX]);

        kvClientPort = Integer.valueOf(cliArguments[CLI_KVCLIENT_PORT_INDEX]);
        kvServerPort = Integer.valueOf(cliArguments[CLI_KVSERVER_PORT_INDEX]);
        kvECSPort = Integer.valueOf(cliArguments[CLI_KVECS_PORT_INDEX]);

        if (cliArguments.length == ArgumentsValidator.REQUIRED_CLI_ARGUMENT_NUMBER_WITH_LOADBALANCER) {
            loadbalancerIp = cliArguments[CLI_LOADBALANCER_IP_INDEX];
            loadbalancerPort = Integer.valueOf(cliArguments[CLI_LOADBALANCER_PORT_INDEX]);
        }
    }

    public String getServerName() {
        return serverName;
    }

    public DataAccessServiceInitializationContext getDASInitializationContext() {
        return new DataAccessServiceInitializationContext.Builder().cacheSize(cacheSize)
                .displacementStrategy(displacementStrategy)
                .rootFolderPath(KVServer.PERSISTENT_STORAGE_DEFAULT_ROOT_FOLDER).build();
    }

    public KVServerPortContext getPortConfigurationContext() {
        return new KVServerPortContext.Builder().clientPort(kvClientPort).serverPort(kvServerPort)
                .ecsPort(kvECSPort).build();
    }

    public IReplicableDataAccessService getReplicableDAS() {
        ReplicationServiceFactory replicationServiceFactory = new ReplicationServiceFactory();
        ReplicationService replicationService =
                replicationServiceFactory.createReplicationServiceWith2PC();
        return new DataAccessServiceFactory().createReplicableDataAccessServiceWithEncryption(
                getDASInitializationContext(), replicationService);
    }

    public ServerConnectionInfo getLoadbalancerConnectionInfo() throws UnknownHostException {
        if (loadbalancerIp == null) {
            return null;
        } else {
            return new ServerConnectionInfo.Builder().ipAddress(loadbalancerIp)
                    .port(loadbalancerPort).build();
        }
    }

}
