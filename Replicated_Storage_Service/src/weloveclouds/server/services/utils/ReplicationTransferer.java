package weloveclouds.server.services.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * An implementation of a {@link IReplicationTransferer} which can replicate the requests on
 * replicas.
 * 
 * @author Benedek
 */
public class ReplicationTransferer implements IReplicationTransferer {

    private static final Logger LOGGER = Logger.getLogger(ReplicationTransferer.class);

    private IConcurrentCommunicationApi concurrentCommunicationApi;
    private ConnectionFactory connectionFactory;
    private Set<ServerConnectionInfo> replicaConnectionInfos;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

    private ExecutorService executorService;

    public ReplicationTransferer(IConcurrentCommunicationApi concurrentCommunicationApi,
            ConnectionFactory connectionFactory, Set<ServerConnectionInfo> replicaConnectionInfos,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
        this.concurrentCommunicationApi = concurrentCommunicationApi;
        this.connectionFactory = connectionFactory;
        this.replicaConnectionInfos = replicaConnectionInfos;
        this.transferMessageSerializer = transferMessageSerializer;
        this.transferMessageDeserializer = transferMessageDeserializer;

        this.executorService = Executors.newFixedThreadPool(replicaConnectionInfos.size());
    }

    @Override
    public void putEntryOnReplicas(KVEntry entry) {
        Set<AbstractReplicationRequest<?>> replicationRequests = new HashSet<>();

        for (ServerConnectionInfo connectionInfo : replicaConnectionInfos) {
            try {
                Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                replicationRequests.add(new PutReplicationRequest(concurrentCommunicationApi,
                        connection, entry, transferMessageSerializer, transferMessageDeserializer));
            } catch (IOException ex) {
                LOGGER.error(CustomStringJoiner.join(" ", "Exception (", ex.toString(),
                        ") occured while replicating PUT (", entry.toString(), ") on",
                        connectionInfo.toString()));
            }
        }

        submitExecutorTasks(replicationRequests);
    }

    @Override
    public void removeEntryOnReplicas(String key) {
        Set<AbstractReplicationRequest<?>> replicationRequests = new HashSet<>();

        for (ServerConnectionInfo connectionInfo : replicaConnectionInfos) {
            try {
                Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                replicationRequests.add(new DeleteReplicationRequest(concurrentCommunicationApi,
                        connection, key, transferMessageSerializer, transferMessageDeserializer));
            } catch (IOException ex) {
                LOGGER.error(CustomStringJoiner.join(" ", "Exception (", ex.toString(),
                        ") occured while replicating DELETE (", key, ") on",
                        connectionInfo.toString()));
            }
        }

        submitExecutorTasks(replicationRequests);
    }

    /**
     * Submits the respective replication requests on the {@link #executorService} and waits until
     * all of them is completed.
     */
    private void submitExecutorTasks(Set<AbstractReplicationRequest<?>> replicationRequests) {
        Collection<Future<?>> futures = new HashSet<>();

        for (AbstractReplicationRequest<?> replicationRequest : replicationRequests) {
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
        futures.clear();
    }
}
