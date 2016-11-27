package weloveclouds.server.models.commands;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerSocketFactory;
import weloveclouds.server.models.conf.KVServerCLIContext;
import weloveclouds.server.models.requests.kvclient.IKVClientRequest;
import weloveclouds.server.models.requests.kvclient.KVClientRequestFactory;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.DataAccessServiceFactory;
import weloveclouds.server.services.models.DataAccessServiceInitializationContext;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.utils.ArgumentsValidator;

/**
 * StartNode command which starts the {@link Server}} based on the configuration in
 * {@link #context}.
 *
 * @author Benedek
 */
public class Start extends AbstractServerCommand {

    private static final Logger LOGGER = Logger.getLogger(Start.class);

    private DataAccessServiceFactory dataAccessServiceFactory;
    private KVServerCLIContext context;

    /**
     * @param context contains the server parameter configuration
     */
    public Start(String[] arguments, DataAccessServiceFactory dataAccessServiceFactory,
            KVServerCLIContext context) {
        super(arguments);
        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.context = context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing start command.");

            int port = context.getPort();
            int cacheSize = context.getCacheSize();
            DisplacementStrategy startegy = context.getDisplacementStrategy();
            Path storagePath = context.getStoragePath();

            DataAccessServiceInitializationContext initializationContext =
                    new DataAccessServiceInitializationContext.Builder().cacheSize(cacheSize)
                            .displacementStrategy(startegy).rootFolderPath(storagePath).build();

            DataAccessService dataAccessService = dataAccessServiceFactory
                    .createInitializedDataAccessService(initializationContext);

            Server<KVMessage, IKVClientRequest> server =
                    new Server.Builder<KVMessage, IKVClientRequest>().port(port)
                            .serverSocketFactory(new ServerSocketFactory())
                            .requestFactory(new KVClientRequestFactory(dataAccessService))
                            .communicationApiFactory(new CommunicationApiFactory())
                            .messageSerializer(new KVMessageSerializer())
                            .messageDeserializer(new KVMessageDeserializer()).build();
            server.start();

            context.setStarted(true);
            String statusMessage = "Server is running.";
            userOutputWriter.writeLine(statusMessage);
            LOGGER.info(statusMessage);
        } catch (IOException ex) {
            context.setStarted(false);
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("start command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStartArguments(arguments, context);
        return this;
    }


}
