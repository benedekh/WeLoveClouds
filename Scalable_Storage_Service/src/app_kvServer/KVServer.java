package app_kvServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerCLIHandler;
import weloveclouds.server.core.ServerSocketFactory;
import weloveclouds.server.models.commands.ServerCommandFactory;
import weloveclouds.server.models.requests.DataServiceRequestFactory;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.store.MovablePersistentStorage;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.FIFOStrategy;
import weloveclouds.server.store.cache.strategy.LFUStrategy;
import weloveclouds.server.store.cache.strategy.LRUStrategy;
import weloveclouds.server.utils.LogSetup;

/**
 * 
 * Server application. See {@link ServerCLIHandler} for more details.
 * 
 * @author Benoit, Benedek, Hunton
 */
public class KVServer {

    /**
     * The entry point of the application.
     * 
     * @param args is discarded so far
     */
    public static void main(String[] args) {
        String logFile = "logs/server.log";
        try {
            new LogSetup(logFile, Level.OFF);
            ServerCLIHandler cli = new ServerCLIHandler(System.in, new ServerCommandFactory());
            cli.run();
        } catch (IOException ex) {
            System.err.println(CustomStringJoiner.join(" ", "Log file cannot be created on path ",
                    logFile, "due to an error:", ex.getMessage()));
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

        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException(
                    "Invalid port number. Valid value is between 0 and 65535.");
        }

        KVCache cache = new KVCache(cacheSize, displacementStrategy);
        KVPersistentStorage persistentStorage =
                new MovablePersistentStorage(defaultStoragePath);
        DataAccessService dataAccessService = new DataAccessService(cache, persistentStorage);

        try {
            Server server = new Server.ServerBuilder().port(port)
                    .serverSocketFactory(new ServerSocketFactory())
                    .requestFactory(new DataServiceRequestFactory(dataAccessService))
                    .communicationApiFactory(new CommunicationApiFactory())
                    .messageSerializer(new KVMessageSerializer())
                    .messageDeserializer(new KVMessageDeserializer()).build();
            server.start();
        } catch (IOException e) {
            Logger.getLogger(getClass()).error(e);
        }

    }


}
