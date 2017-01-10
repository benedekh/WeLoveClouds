package weloveclouds.server.client.commands;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.KVHeartbeatMessageSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.ServiceHealthInfosSerializer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerCLIContext;
import weloveclouds.server.core.KVServer;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerFactory;
import weloveclouds.server.monitoring.NodeHealthMonitor;
import weloveclouds.server.services.datastore.DataAccessServiceFactory;
import weloveclouds.server.services.datastore.IReplicableDataAccessService;
import weloveclouds.server.services.datastore.models.DataAccessServiceInitializationContext;
import weloveclouds.server.services.replication.ReplicationService;
import weloveclouds.server.services.replication.ReplicationServiceFactory;


/**
 * StartNode command which starts the {@link Server} based on the configuration in {@link #context}.
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

            DataAccessServiceInitializationContext initializationContext =
                    new DataAccessServiceInitializationContext.Builder()
                            .cacheSize(context.getCacheSize())
                            .displacementStrategy(context.getDisplacementStrategy())
                            .rootFolderPath(context.getStoragePath()).build();

            ReplicationServiceFactory replicationServiceFactory = new ReplicationServiceFactory();
            ReplicationService replicationService =
                    replicationServiceFactory.createReplicationServiceWith2PC();

            IReplicableDataAccessService dataAccessService =
                    dataAccessServiceFactory.createInitializedReplicableDataAccessService(
                            initializationContext, replicationService);

            ServerConnectionInfo loadbalancerFakeConnectionInfo =
                    new ServerConnectionInfo.Builder().ipAddress("localhost").port(8008).build();
            NodeHealthMonitor.Builder nodeHealthMonitorBuilder = new NodeHealthMonitor.Builder()
                    .communicationApi(new CommunicationApiFactory().createCommunicationApiV1())
                    .heartbeatSerializer(new KVHeartbeatMessageSerializer(
                            new NodeHealthInfosSerializer(new ServiceHealthInfosSerializer(
                                    new ServerConnectionInfoSerializer()))))
                    .nodeHealthInfosBuilder(
                            new NodeHealthInfos.Builder().nodeName(UUID.randomUUID().toString()))
                    .loadbalancerConnectionInfo(loadbalancerFakeConnectionInfo);

            KVServer kvServer = new KVServer.Builder().serverFactory(serverFactory)
                    .portConfiguration(context.getPortContext())
                    .dataAccessService(dataAccessService)
                    .nodeHealthMonitorBuilder(nodeHealthMonitorBuilder).build();
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
