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

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;

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

    private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer;

    private ExecutorService executorService;

    protected ReplicationTransferer(Builder builder) {
        this.concurrentCommunicationApi = builder.concurrentCommunicationApi;
        this.connectionFactory = builder.connectionFactory;
        this.replicaConnectionInfos = builder.replicaConnectionInfos;
        this.transferMessageSerializer = builder.transferMessageSerializer;
        this.transferMessageDeserializer = builder.transferMessageDeserializer;

        this.executorService = Executors.newFixedThreadPool(replicaConnectionInfos.size());
    }

    @Override
    public void putEntryOnReplicas(KVEntry entry) {
        Set<AbstractReplicationRequest<?, ?>> replicationRequests = new HashSet<>();

        for (ServerConnectionInfo connectionInfo : replicaConnectionInfos) {
            try {
                Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                replicationRequests.add(new PutReplicationRequest.Builder()
                        .communicationApi(concurrentCommunicationApi).connection(connection)
                        .payload(entry).messageSerializer(transferMessageSerializer)
                        .messageDeserializer(transferMessageDeserializer).build());
            } catch (IOException ex) {
                LOGGER.error(StringUtils.join(" ", "Exception (", ex.toString(),
                        ") occured while replicating PUT (", entry.toString(), ") on",
                        connectionInfo.toString()));
            }
        }

        submitExecutorTasks(replicationRequests);
    }

    @Override
    public void removeEntryOnReplicas(String key) {
        Set<AbstractReplicationRequest<?, ?>> replicationRequests = new HashSet<>();

        for (ServerConnectionInfo connectionInfo : replicaConnectionInfos) {
            try {
                Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                replicationRequests.add(new DeleteReplicationRequest.Builder()
                        .communicationApi(concurrentCommunicationApi).connection(connection)
                        .payload(key).messageSerializer(transferMessageSerializer)
                        .messageDeserializer(transferMessageDeserializer).build());
            } catch (IOException ex) {
                LOGGER.error(StringUtils.join(" ", "Exception (", ex.toString(),
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
    private void submitExecutorTasks(Set<AbstractReplicationRequest<?, ?>> replicationRequests) {
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
        futures.clear();
    }

    /**
     * Builder pattern for creating a {@link ReplicationTransferer} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IConcurrentCommunicationApi concurrentCommunicationApi;
        private ConnectionFactory connectionFactory;
        private Set<ServerConnectionInfo> replicaConnectionInfos;
        private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;
        private IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer;

        public Builder communicationApi(IConcurrentCommunicationApi concurrentCommunicationApi) {
            this.concurrentCommunicationApi = concurrentCommunicationApi;
            return this;
        }

        public Builder connectionFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public Builder replicaConnectionInfos(Set<ServerConnectionInfo> replicaConnectionInfos) {
            this.replicaConnectionInfos = replicaConnectionInfos;
            return this;
        }

        public Builder messageSerializer(
                IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer) {
            this.transferMessageSerializer = transferMessageSerializer;
            return this;
        }

        public Builder messageDeserializer(
                IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer) {
            this.transferMessageDeserializer = transferMessageDeserializer;
            return this;
        }

        public ReplicationTransferer build() {
            return new ReplicationTransferer(this);
        }
    }
}
