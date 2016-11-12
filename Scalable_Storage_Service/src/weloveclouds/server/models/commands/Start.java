package weloveclouds.server.models.commands;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.serialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerSocketFactory;
import weloveclouds.server.models.ServerConfigurationContext;
import weloveclouds.server.models.exceptions.ServerSideException;
import weloveclouds.server.models.requests.RequestFactory;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.store.ControllablePersistentStorage;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.utils.ArgumentsValidator;

/**
 * Start command which starts the {@link Server}} based on the configuration in {@link #context}.
 * 
 * @author Benedek
 */
public class Start extends AbstractServerCommand {

    private ServerConfigurationContext context;
    private Logger logger;

    /**
     * @param context contains the server parameter configuration
     */
    public Start(String[] arguments, ServerConfigurationContext context) {
        super(arguments);
        this.context = context;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing start command.");

            int port = context.getPort();
            int cacheSize = context.getCacheSize();
            DisplacementStrategy startegy = context.getDisplacementStrategy();
            Path storagePath = context.getStoragePath();

            KVCache cache = new KVCache(cacheSize, startegy);
            KVPersistentStorage persistentStorage = new ControllablePersistentStorage(storagePath);
            DataAccessService dataAccessService = new DataAccessService(cache, persistentStorage);

            Server server = new Server.ServerBuilder().port(port)
                    .serverSocketFactory(new ServerSocketFactory())
                    .requestFactory(new RequestFactory(dataAccessService))
                    .communicationApiFactory(new CommunicationApiFactory())
                    .messageSerializer(new KVMessageSerializer())
                    .messageDeserializer(new KVMessageDeserializer()).build();
            server.start();

            context.setStarted(true);
            String statusMessage = "Server is running.";
            userOutputWriter.writeLine(statusMessage);
            logger.info(statusMessage);
        } catch (IOException ex) {
            context.setStarted(false);
            logger.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            logger.info("start command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStartArguments(arguments, context);
        return this;
    }


}
