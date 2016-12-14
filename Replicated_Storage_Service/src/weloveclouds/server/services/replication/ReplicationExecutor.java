package weloveclouds.server.services.replication;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;


public class ReplicationExecutor {

    private static final Logger LOGGER = Logger.getLogger(ReplicationExecutor.class);

    private ConnectionFactory connectionFactory;
    private ExecutorService executorService;

    public ReplicationExecutor(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void executeRequest(AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> replicationConnectionInfos) {
        if (replicationConnectionInfos != null && !replicationConnectionInfos.isEmpty()) {
            Set<AbstractReplicationRequest<?, ?>> requests =
                    initializeConnections(replicationRequest, replicationConnectionInfos);
            submitExecutorTasks(requests);
        }
    }

    private Set<AbstractReplicationRequest<?, ?>> initializeConnections(
            AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> replicationConnectionInfos) {
        Set<AbstractReplicationRequest<?, ?>> replicationRequests = new HashSet<>();
        for (ServerConnectionInfo connectionInfo : replicationConnectionInfos) {
            try {
                Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                AbstractReplicationRequest<?, ?> request = replicationRequest.clone();
                request.setConnection(connection);
                replicationRequests.add(request);
            } catch (IOException ex) {
                LOGGER.error(StringUtils.join("", "Cannot create connection to (", connectionInfo,
                        ") for replication request (", replicationRequest, ")"));
                return new HashSet<>();
            }
        }
        return replicationRequests;

    }

    private void submitExecutorTasks(Set<AbstractReplicationRequest<?, ?>> replicationRequests) {
        if (!replicationRequests.isEmpty()) {
            executorService = Executors.newFixedThreadPool(replicationRequests.size());

            Collection<Future<?>> futures = new HashSet<>();
            for (AbstractReplicationRequest<?, ?> replicationRequest : replicationRequests) {
                Future<?> future = executorService.submit(replicationRequest);
                futures.add(future);
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.error(ex);
                }
            }

            executorService.shutdown();
        }
    }

}
