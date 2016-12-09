package weloveclouds.server.core;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.models.configuration.KVServerPortContext;
import weloveclouds.server.requests.kvclient.IKVClientRequest;
import weloveclouds.server.requests.kvecs.IKVECSRequest;
import weloveclouds.server.requests.kvserver.IKVServerRequest;
import weloveclouds.server.services.IReplicableDataAccessService;

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

    private Server<KVMessage, IKVClientRequest> kvClientRequestsServer;
    private Server<KVTransferMessage, IKVServerRequest> kvServerRequestsServer;
    private Server<KVAdminMessage, IKVECSRequest> kvECSRequestsServer;

    public KVServer(ServerFactory serverFactory, KVServerPortContext portConfiguration,
            IReplicableDataAccessService dataAccessService) throws IOException {
        LOGGER.debug("Creating the servers for the different requests.");
        this.kvClientRequestsServer = serverFactory.createServerForKVClientRequests(
                portConfiguration.getKVClientPort(), dataAccessService);
        this.kvServerRequestsServer = serverFactory.createServerForKVServerRequests(
                portConfiguration.getKVServerPort(), dataAccessService);
        this.kvECSRequestsServer = serverFactory
                .createServerForKVECSRequests(portConfiguration.getKVECSPort(), dataAccessService);
        LOGGER.debug("Creating the servers for the different requests finished.");
    }

    public void start() {
        LOGGER.debug("Starting the servers for the different requests.");
        kvClientRequestsServer.start();
        kvServerRequestsServer.start();
        kvECSRequestsServer.start();
        LOGGER.debug("Servers shall be running.");
    }

}
