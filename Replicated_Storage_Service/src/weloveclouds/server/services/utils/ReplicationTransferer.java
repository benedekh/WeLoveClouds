package weloveclouds.server.services.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

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
            replicationRequests.add(new PutReplicationRequest(connectionInfo,
                    concurrentCommunicationApi, connectionFactory, transferMessageSerializer,
                    transferMessageDeserializer, entry));
        }

        submitExecutorTasks(replicationRequests);
    }

    @Override
    public void removeKeyOnReplicas(String key) {
        Set<AbstractReplicationRequest<?>> replicationRequests = new HashSet<>();

        for (ServerConnectionInfo connectionInfo : replicaConnectionInfos) {
            replicationRequests.add(new RemoveReplicationRequest(connectionInfo,
                    concurrentCommunicationApi, connectionFactory, transferMessageSerializer,
                    transferMessageDeserializer, key));
        }

        submitExecutorTasks(replicationRequests);
    }

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
