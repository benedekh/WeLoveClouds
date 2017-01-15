package weloveclouds.server.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;
import weloveclouds.server.configuration.models.KVServerPortContext;
import weloveclouds.server.monitoring.heartbeat.NodeHealthMonitor;
import weloveclouds.server.monitoring.heartbeat.ServiceHealthMonitor;
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

    private NodeHealthMonitor nodeHealthMonitor;

    protected KVServer(Builder builder) throws IOException {
        LOGGER.debug("Creating the servers for the different requests.");

        int kvClientPort = builder.portConfiguration.getKVClientPort();
        ServiceHealthMonitor kvClientHealthMonitor =
                createServiceHealthMonitor(kvClientPort, "kvclient_requests", 1);
        this.kvClientRequestsServer = builder.serverFactory.createServerForKVClientRequests(
                kvClientPort, builder.dataAccessService, kvClientHealthMonitor);

        int kvServerPort = builder.portConfiguration.getKVServerPort();
        ServiceHealthMonitor kvServerHealthMonitor =
                createServiceHealthMonitor(kvServerPort, "kvserver_requests", 2);
        this.kvServerRequestsServer = builder.serverFactory.createServerForKVServerRequests(
                kvServerPort, builder.dataAccessService, kvServerHealthMonitor);

        int kvECSPort = builder.portConfiguration.getKVECSPort();
        ServiceHealthMonitor kvECSHealthMonitor =
                createServiceHealthMonitor(kvECSPort, "kvecs_requests", 3);
        this.kvECSRequestsServer = builder.serverFactory.createServerForKVECSRequests(kvECSPort,
                builder.dataAccessService, kvECSHealthMonitor);

        builder.nodeHealthMonitorBuilder.serviceHealthMonitors(
                Arrays.asList(kvClientHealthMonitor, kvServerHealthMonitor, kvECSHealthMonitor));
        this.nodeHealthMonitor = builder.nodeHealthMonitorBuilder.build();
        this.nodeHealthMonitor.setNodeStatus(NodeStatus.RUNNING);

        LOGGER.debug("Creating the servers for the different requests finished.");
    }

    private ServiceHealthMonitor createServiceHealthMonitor(int servicePort, String serviceName,
            int servicePriority) {
        try {
            ServiceHealthInfos.Builder healthInfosBuilder = new ServiceHealthInfos.Builder()
                    .serviceEnpoint(new ServerConnectionInfo.Builder()
                            .ipAddress(InetAddress.getLocalHost()).port(servicePort).build())
                    .serviceName(serviceName).servicePriority(servicePriority)
                    .serviceStatus(ServiceStatus.INITIALIZED);
            return new ServiceHealthMonitor(healthInfosBuilder);
        } catch (UnknownHostException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    /**
     * Starts the KVServer.
     */
    public void start() {
        LOGGER.debug("Starting the servers for the different requests.");
        kvClientRequestsServer.start();
        kvServerRequestsServer.start();
        kvECSRequestsServer.start();
        nodeHealthMonitor.start();
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
        private NodeHealthMonitor.Builder nodeHealthMonitorBuilder;

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

        public Builder nodeHealthMonitorBuilder(
                NodeHealthMonitor.Builder nodeHealthMonitorBuilder) {
            this.nodeHealthMonitorBuilder = nodeHealthMonitorBuilder;
            return this;
        }

        public KVServer build() throws IOException {
            return new KVServer(this);
        }
    }

}
