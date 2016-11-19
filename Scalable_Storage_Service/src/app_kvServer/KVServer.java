package app_kvServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerCLIHandler;
import weloveclouds.server.core.ServerFactory;
import weloveclouds.server.models.commands.ServerCommandFactory;
import weloveclouds.server.models.requests.kvclient.IKVClientRequest;
import weloveclouds.server.models.requests.kvecs.IKVECSRequest;
import weloveclouds.server.models.requests.kvserver.IKVServerRequest;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.DataAccessServiceFactory;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.FIFOStrategy;
import weloveclouds.server.store.cache.strategy.LFUStrategy;
import weloveclouds.server.store.cache.strategy.LRUStrategy;
import weloveclouds.server.utils.ArgumentsValidator;
import weloveclouds.server.utils.LogSetup;

/**
 * 
 * Server application. See {@link ServerCLIHandler} for more details.
 * 
 * @author Benoit, Benedek, Hunton
 */
public class KVServer {

    private static final String DEFAULT_LOG_PATH = "logs/server.log";

    private static final int KVSERVER_REQUESTS_PORT = 50001;
    private static final int ECS_REQUESTS_PORT = 50002;

    private static final int CLI_PORT_INDEX = 0;
    private static final int CLI_LOG_LEVEL_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(KVServer.class);

    /**
     * The entry point of the application.
     * 
     * @param args is discarded so far
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            startInteractiveCLIMode();
        } else {
            startNonInteractiveMode(args);
        }
    }

    /**
     * Start the service with a non-interactive mode, directly from the command-line.
     * 
     * @param cliArguments array of the command line arguments
     */
    private static void startNonInteractiveMode(String[] cliArguments) {
        try {
            ArgumentsValidator.validateCLIArgumentsForServerStart(cliArguments);

            initializeLoggerWithLevel(cliArguments[CLI_LOG_LEVEL_INDEX]);
            int port = Integer.valueOf(cliArguments[CLI_PORT_INDEX]);

            ServerFactory serverFactory = new ServerFactory();
            IMovableDataAccessService dataAccessService =
                    new DataAccessServiceFactory().createUninitializedMovableDataAccessService();

            Server<KVAdminMessage, IKVECSRequest> serverForECSRequests = serverFactory
                    .createServerForKVECSRequests(ECS_REQUESTS_PORT, dataAccessService);
            Server<KVTransferMessage, IKVServerRequest> serverForKVServerRequests = serverFactory
                    .createServerForKVServerRequests(KVSERVER_REQUESTS_PORT, dataAccessService);
            Server<KVMessage, IKVClientRequest> serverForKVClientRequests =
                    serverFactory.createServerForKVClientRequests(port, dataAccessService);

            serverForECSRequests.start();
            serverForKVServerRequests.start();
            serverForKVClientRequests.start();
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Starts the service with interactive command-line mode.
     */
    private static void startInteractiveCLIMode() {
        initializeLoggerWithLevel(Level.OFF);
        ServerCLIHandler cli = new ServerCLIHandler(System.in,
                new ServerCommandFactory(new DataAccessServiceFactory()));
        cli.run();
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
            System.err.println(CustomStringJoiner.join(" ", "Log file cannot be created on path ",
                    DEFAULT_LOG_PATH, "due to an error:", ex.getMessage()));
        }
    }

    /**
     * Start KV Server at given port. ONLY FOR TESTING PURPOSES!!!
     *
     * @param port given port for storage server to operate
     * @param cacheSize specifies how many key-value pairs the server is allowed to keep in-memory
     * @param strategy specifies the cache replacement strategy in case the cache is full and there
     *        is a GET- or PUT-request on a key that is currently not contained in the cache.
     *        Options are "FIFO", "LRU", and "LFU".
     */
    public KVServer(int port, int cacheSize, String strategy) {
        Path defaultStoragePath = Paths.get("logs/testing/");
        if (!defaultStoragePath.toAbsolutePath().toFile().exists()) {
            defaultStoragePath.toAbsolutePath().toFile().mkdirs();
        }

        DisplacementStrategy displacementStrategy = null;

        switch (strategy) {
            case "FIFO":
                displacementStrategy = new FIFOStrategy();
                break;
            case "LRU":
                displacementStrategy = new LRUStrategy();
                break;
            case "LFU":
                displacementStrategy = new LFUStrategy();
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid strategy. Valid values are: FIFO, LRU, LFU");
        }

        ArgumentsValidator.validatePortArguments(new String[] {String.valueOf(port)});

        DataAccessServiceInitializationContext initializationContext =
                new DataAccessServiceInitializationContext.Builder().cacheSize(cacheSize)
                        .displacementStrategy(displacementStrategy)
                        .rootFolderPath(defaultStoragePath).build();

        DataAccessService dataAccessService = new DataAccessServiceFactory()
                .createInitializedDataAccessService(initializationContext);

        try {
            Server<KVMessage, IKVClientRequest> server =
                    new ServerFactory().createServerForKVClientRequests(port, dataAccessService);
            server.start();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}
