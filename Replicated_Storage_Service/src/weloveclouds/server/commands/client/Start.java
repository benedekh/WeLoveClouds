package weloveclouds.server.commands.client;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.server.core.KVServer;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerFactory;
import weloveclouds.server.models.configuration.KVServerCLIContext;
import weloveclouds.server.models.configuration.KVServerPortContext;
import weloveclouds.server.services.DataAccessServiceFactory;
import weloveclouds.server.services.IReplicableDataAccessService;
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

    private ServerFactory serverFactory;
    private DataAccessServiceFactory dataAccessServiceFactory;
    private KVServerCLIContext context;

    protected Start(Builder builder) {
        super(builder.arguments);
        this.serverFactory = builder.serverFactory;
        this.dataAccessServiceFactory = builder.dataAccessServiceFactory;
        this.context = builder.context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing start command.");

            KVServerPortContext portContext = context.getPortContext();
            DisplacementStrategy startegy = context.getDisplacementStrategy();
            Path storagePath = context.getStoragePath();
            int cacheSize = context.getCacheSize();

            DataAccessServiceInitializationContext initializationContext =
                    new DataAccessServiceInitializationContext.Builder().cacheSize(cacheSize)
                            .displacementStrategy(startegy).rootFolderPath(storagePath).build();
            IReplicableDataAccessService dataAccessService = dataAccessServiceFactory
                    .createInitializedReplicableDataAccessService(initializationContext);

            KVServer kvServer = new KVServer.Builder().serverFactory(serverFactory)
                    .portConfiguration(portContext).dataAccessService(dataAccessService).build();
            kvServer.start();

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

    /**
     * Builder pattern for creating a {@link Start} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private String[] arguments;
        private ServerFactory serverFactory;
        private DataAccessServiceFactory dataAccessServiceFactory;
        private KVServerCLIContext context;

        public Builder arguments(String[] arguments) {
            this.arguments = arguments;
            return this;
        }

        /**
         * @param serverFactory factory to create every servers a KVServer need to start
         */
        public Builder serverFactory(ServerFactory serverFactory) {
            this.serverFactory = serverFactory;
            return this;
        }

        /**
         * @param dataAccessServiceFactory factory to create a Data Access Service instance
         */
        public Builder dataAccessServiceFactory(DataAccessServiceFactory dataAccessServiceFactory) {
            this.dataAccessServiceFactory = dataAccessServiceFactory;
            return this;
        }

        /**
         * @param context contains the server parameter configuration
         */
        public Builder serverCLIContext(KVServerCLIContext context) {
            this.context = context;
            return this;
        }

        public Start build() {
            return new Start(this);
        }


    }


}
