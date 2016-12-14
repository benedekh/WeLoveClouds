package weloveclouds.server.core;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.server.configuration.models.KVServerPortContext;
import weloveclouds.server.services.datastore.IReplicableDataAccessService;


/**
 * Encapsulates all three server instances which are needed for the whole KVServer functionality.
 * <br>
 * One server to serve requests coming from the KVClients.<br>
 * One server to serve requests coming from other KVServers.<br>
 * One server to serve requests coming from the KVECS.
 *
 * @author Benedek
 */
public class KVServer {

    private static final Logger LOGGER = Logger.getLogger(KVServer.class);

    private AbstractServer<?> kvClientRequestsServer;
    private AbstractServer<?> kvServerRequestsServer;
    private AbstractServer<?> kvECSRequestsServer;

    protected KVServer(Builder builder) throws IOException {
        LOGGER.debug("Creating the servers for the different requests.");
        this.kvClientRequestsServer = builder.serverFactory.createServerForKVClientRequests(
                builder.portConfiguration.getKVClientPort(), builder.dataAccessService);
        this.kvServerRequestsServer = builder.serverFactory.createServerForKVServerRequests(
                builder.portConfiguration.getKVServerPort(), builder.dataAccessService);
        this.kvECSRequestsServer = builder.serverFactory.createServerForKVECSRequests(
                builder.portConfiguration.getKVECSPort(), builder.dataAccessService);
        LOGGER.debug("Creating the servers for the different requests finished.");
    }

    public void start() {
        LOGGER.debug("Starting the servers for the different requests.");
        kvClientRequestsServer.start();
        kvServerRequestsServer.start();
        kvECSRequestsServer.start();
        LOGGER.debug("Servers shall be running.");
    }

    /**
     * Builder pattern for creating a {@link KVServer} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ServerFactory serverFactory;
        private KVServerPortContext portConfiguration;
        private IReplicableDataAccessService dataAccessService;


        public Builder serverFactory(ServerFactory serverFactory) {
            this.serverFactory = serverFactory;
            return this;
        }

        public Builder portConfiguration(KVServerPortContext portConfiguration) {
            this.portConfiguration = portConfiguration;
            return this;
        }

        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public KVServer build() throws IOException {
            return new KVServer(this);
        }
    }

}
