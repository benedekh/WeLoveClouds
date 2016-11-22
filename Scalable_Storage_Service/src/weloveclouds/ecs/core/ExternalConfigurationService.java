package weloveclouds.ecs.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import weloveclouds.cli.utils.UserOutputWriter;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;
import weloveclouds.ecs.exceptions.InvalidConfigurationException;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
import weloveclouds.ecs.models.commands.internal.InitNodeMetadata;
import weloveclouds.ecs.models.commands.internal.ShutdownNode;
import weloveclouds.ecs.models.commands.internal.StartNode;
import weloveclouds.ecs.models.commands.internal.StopNode;
import weloveclouds.ecs.models.commands.internal.ssh.LaunchJar;
import weloveclouds.ecs.models.repository.EcsRepository;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.repository.StorageNodeStatus;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.BatchRetryableTasks;
import weloveclouds.ecs.models.tasks.AbstractBatchTasks;
import weloveclouds.ecs.models.tasks.SimpleRetryableTask;
import weloveclouds.ecs.services.ISecureShellService;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.ListUtils;
import weloveclouds.ecs.utils.RingMetadataHelper;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.ecs.core.EcsStatus.ADDING_NODE;
import static weloveclouds.ecs.core.EcsStatus.INITIALIZING_SERVICE;
import static weloveclouds.ecs.core.EcsStatus.REMOVING_NODE;
import static weloveclouds.ecs.core.EcsStatus.SHUTDOWNING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STARTING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STOPPING_NODE;
import static weloveclouds.ecs.core.EcsStatus.UNINITIALIZED;
import static weloveclouds.ecs.core.ExternalConfigurationServiceConstants.*;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.IDLE;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.RUNNING;
import static weloveclouds.ecs.models.tasks.BatchPurpose.SERVICE_INITIALISATION;
import static weloveclouds.ecs.models.tasks.BatchPurpose.SHUTDOWN;
import static weloveclouds.ecs.models.tasks.BatchPurpose.START_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.STOP_NODE;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService implements Observer {
    private static final String JAR_FILE_PATH = "";
    private EcsStatus status;
    private final HashRange INITIAL_HASHRANGE;
    private RingMetadata ringMetadata;
    private String configurationFilePath;
    private EcsRepository repository;
    private EcsRepositoryFactory ecsRepositoryFactory;
    private ITaskService taskService;
    private CommunicationApiFactory communicationApiFactory;
    private ISecureShellService secureShellService;


    public ExternalConfigurationService(Builder externalConfigurationServiceBuilder) throws ServiceBootstrapException {
        this.taskService = externalConfigurationServiceBuilder.taskService;
        this.communicationApiFactory = externalConfigurationServiceBuilder.communicationApiFactory;
        this.secureShellService = externalConfigurationServiceBuilder.secureShellService;
        this.ecsRepositoryFactory = externalConfigurationServiceBuilder.ecsRepositoryFactory;
        this.configurationFilePath = externalConfigurationServiceBuilder.configurationFilePath;
        this.ringMetadata = new RingMetadata();
        INITIAL_HASHRANGE = new HashRange.Builder().start(Hash.MIN_VALUE).end(Hash.MAX_VALUE)
                .build();
        bootstrapConfiguration();
        this.status = UNINITIALIZED;
    }

    @SuppressWarnings("unchecked")
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) throws
            ExternalConfigurationServiceException {
        if (status == UNINITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> nodeInitialisationBatch = new
                    BatchRetryableTasks(SERVICE_INITIALISATION);
            List<StorageNode> storageNodesToInitialize = (List<StorageNode>) ListUtils
                    .getPreciseNumberOfRandomObjectsFrom(repository.getNodesWithStatus(IDLE), numberOfNodes);

            for (StorageNode storageNode : storageNodesToInitialize) {
                LaunchJar taskCommand = new LaunchJar.Builder()
                        .jarFilePath(JAR_FILE_PATH)
                        .arguments(Arrays.asList(Integer.toString(cacheSize), displacementStrategy))
                        .secureShellService(secureShellService)
                        .targetedNode(storageNode)
                        .build();

                nodeInitialisationBatch.addTask(
                        new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
                nodeInitialisationBatch.addObserver(this);
            }

            taskService.launchBatchTasks(nodeInitialisationBatch);
            status = INITIALIZING_SERVICE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <initService> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    @SuppressWarnings("unchecked")
    public void start() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> nodeStartBatch = new BatchRetryableTasks(START_NODE);

            for (StorageNode storageNode : repository.getNodesWithStatus(INITIALIZED)) {
                StartNode taskCommand = new StartNode.Builder()
                        .targetedNode(storageNode)
                        .communicationApi(communicationApiFactory.createCommunicationApiV1())
                        .build();

                nodeStartBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_START_RETRIES, taskCommand));
            }

            taskService.launchBatchTasks(nodeStartBatch);
            status = STARTING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <start> is not permitted." +
                    " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void stop() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {

            AbstractBatchTasks<AbstractRetryableTask> nodeStopBatch = new BatchRetryableTasks(STOP_NODE);

            for (StorageNode storageNode : repository.getNodesWithStatus(RUNNING)) {
                StopNode taskCommand = new StopNode.Builder()
                        .targetedNode(storageNode)
                        .communicationApi(communicationApiFactory.createCommunicationApiV1())
                        .build();

                nodeStopBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_STOP_RETRIES, taskCommand));
            }

            taskService.launchBatchTasks(nodeStopBatch);
            status = STOPPING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <stop> is not permitted." +
                    " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void shutDown() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> nodeShutdownBatch = new BatchRetryableTasks(SHUTDOWN);
            List<StorageNodeStatus> activeNodeStatus = Arrays.asList(INITIALIZED, RUNNING);

            for (StorageNode storageNode : repository.getNodeWithStatus(activeNodeStatus)) {
                ShutdownNode taskCommand = new ShutdownNode.Builder()
                        .targetedNode(storageNode)
                        .communicationApi(communicationApiFactory.createCommunicationApiV1())
                        .build();

                nodeShutdownBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES, taskCommand));
            }
            taskService.launchBatchTasks(nodeShutdownBatch);
            status = SHUTDOWNING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <shutdown> is not " +
                    "permitted." + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void addNode(int cacheSize, String displacementStrategy) throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            status = ADDING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <addNode> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    public void removeNode() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            status = REMOVING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <removeNode> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    private void initializeNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch = new
                BatchRetryableTasks(SERVICE_INITIALISATION);

        for (StorageNode storageNode : repository.getNodesWithStatus(INITIALIZED)) {
            InitNodeMetadata taskCommand = new InitNodeMetadata.Builder()
                    .communicationApi(communicationApiFactory.createCommunicationApiV1())
                    .messageSerializer(new KVAdminMessageSerializer())
                    .messageDeserializer(new KVAdminMessageDeserializer())
                    .ringMetadata(ringMetadata)
                    .targetedNode(storageNode)
                    .build();

            nodeMetadataInitialisationBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
            nodeMetadataInitialisationBatch.addObserver(this);
        }

        taskService.launchBatchTasks(nodeMetadataInitialisationBatch);
        status = INITIALIZING_SERVICE;
    }

    private void bootstrapConfiguration() throws ServiceBootstrapException {
        try {
            repository = ecsRepositoryFactory.createEcsRepositoryFrom(new File(configurationFilePath));
        } catch (InvalidConfigurationException ex) {
            throw new ServiceBootstrapException("Bootstrap failed. Unable to start the service : "
                    + ex.getMessage(), ex);
        }
    }

    private void initializeRingMetadata() {
        List<StorageNode> ringOrderedNodes = RingMetadataHelper.computeRingOrder(repository
                .getNodesWithStatus(INITIALIZED));
        HashRange previousRange = null;

        for (StorageNode node : ringOrderedNodes) {
            HashRange hashRange;
            int ringPosition = ringOrderedNodes.indexOf(node);

            hashRange = RingMetadataHelper.computeHashRangeForNodeBasedOnRingPosition(ringPosition,
                    ringOrderedNodes.size(), node.getHashKey(), previousRange);
            node.setHashRange(hashRange);

            ringMetadata.addRangeInfo(new RingMetadataPart.Builder().connectionInfo(node
                    .getServerConnectionInfo()).range(hashRange).build());

            previousRange = hashRange;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable obs, Object obj) {
        AbstractBatchTasks<AbstractRetryableTask> batch = (AbstractBatchTasks<AbstractRetryableTask>) obs;
        List<AbstractRetryableTask> failedTasks = (List<AbstractRetryableTask>) obj;

        if (failedTasks.isEmpty()) {
            displayToUser(CustomStringJoiner.join(" ", batch.toString(), "Ended successfully."));
        } else {
            displayToUser(CustomStringJoiner.join(" ", batch.toString(), "Ended with",
                    String.valueOf(failedTasks.size()), "failed tasks."));
        }

        switch (batch.getPurpose()) {
            case SERVICE_INITIALISATION:
                initializeRingMetadata();
                initializeNodesWithMetadata();
                status = EcsStatus.INITIALIZED;
                break;
            case START_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case STOP_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case REMOVE_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case ADD_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case SHUTDOWN:
                status = EcsStatus.INITIALIZED;
                break;
        }
    }

    private void displayToUser(String message) {
        try {
            UserOutputWriter.getInstance().appendToLine(message);
        } catch (IOException ex) {
            //Log
        }
    }

    public static class Builder {
        private String configurationFilePath;
        private EcsRepositoryFactory ecsRepositoryFactory;
        private ITaskService taskService;
        private CommunicationApiFactory communicationApiFactory;
        private ISecureShellService secureShellService;

        public Builder CommunicationApiFactory(CommunicationApiFactory communicationApiFactory) {
            this.communicationApiFactory = communicationApiFactory;
            return this;
        }

        public Builder secureShellService(ISecureShellService secureShellService) {
            this.secureShellService = secureShellService;
            return this;
        }

        public Builder taskService(ITaskService taskService) {
            this.taskService = taskService;
            return this;
        }

        public Builder ecsRepositoryFactory(EcsRepositoryFactory ecsRepositoryFactory) {
            this.ecsRepositoryFactory = ecsRepositoryFactory;
            return this;
        }

        public Builder configurationFilePath(String configurationFilePath) {
            this.configurationFilePath = configurationFilePath;
            return this;
        }

        public ExternalConfigurationService build() throws ServiceBootstrapException {
            return new ExternalConfigurationService(this);
        }
    }
}
