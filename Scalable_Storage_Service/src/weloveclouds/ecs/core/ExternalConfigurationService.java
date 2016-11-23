package weloveclouds.ecs.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import weloveclouds.cli.utils.UserOutputWriter;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;
import weloveclouds.ecs.exceptions.InvalidConfigurationException;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.internal.EcsInternalCommandFactory;
import weloveclouds.ecs.models.commands.internal.InitNodeMetadata;
import weloveclouds.ecs.models.commands.internal.SetWriteLock;
import weloveclouds.ecs.models.commands.internal.ShutdownNode;
import weloveclouds.ecs.models.commands.internal.StartNode;
import weloveclouds.ecs.models.commands.internal.StopNode;
import weloveclouds.ecs.models.commands.internal.UpdateMetadata;
import weloveclouds.ecs.models.commands.internal.ssh.LaunchJar;
import weloveclouds.ecs.models.repository.EcsRepository;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.repository.StorageNodeStatus;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.BatchRetryableTasks;
import weloveclouds.ecs.models.tasks.AbstractBatchTasks;
import weloveclouds.ecs.models.tasks.SimpleRetryableTask;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.ListUtils;
import weloveclouds.ecs.utils.RingMetadataHelper;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;


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
import static weloveclouds.ecs.models.repository.StorageNodeStatus.REMOVED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.RUNNING;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.WRITELOCKED;
import static weloveclouds.ecs.models.tasks.BatchPurpose.ADD_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.REMOVE_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.SERVICE_INITIALISATION;
import static weloveclouds.ecs.models.tasks.BatchPurpose.SHUTDOWN;
import static weloveclouds.ecs.models.tasks.BatchPurpose.START_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.STOP_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.UPDATING_METADATA;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService implements Observer {
    private EcsStatus status;
    private final HashRange INITIAL_HASHRANGE;
    private RingMetadata ringMetadata;
    private String configurationFilePath;
    private EcsRepository repository;
    private EcsRepositoryFactory ecsRepositoryFactory;
    private ITaskService taskService;
    private EcsInternalCommandFactory ecsInternalCommandFactory;
    private RingTopology<StorageNode> ringTopology;


    public ExternalConfigurationService(Builder externalConfigurationServiceBuilder) throws ServiceBootstrapException {
        this.taskService = externalConfigurationServiceBuilder.taskService;
        this.ecsInternalCommandFactory = externalConfigurationServiceBuilder.ecsInternalCommandFactory;
        this.ecsRepositoryFactory = externalConfigurationServiceBuilder.ecsRepositoryFactory;
        this.configurationFilePath = externalConfigurationServiceBuilder.configurationFilePath;
        this.ringMetadata = new RingMetadata();
        INITIAL_HASHRANGE = new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE)
                .build();
        bootstrapConfiguration();
        this.status = UNINITIALIZED;
        this.ringTopology = new RingTopology<>();
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
                LaunchJar taskCommand = ecsInternalCommandFactory.createLaunchJarCommandWith
                        (storageNode, ExternalConfigurationServiceConstants.KV_SERVER_JAR_PATH, cacheSize,
                                displacementStrategy);

                nodeInitialisationBatch.addTask(
                        new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
            }
            nodeInitialisationBatch.addObserver(this);
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
                StartNode taskCommand = ecsInternalCommandFactory.createStartNodeCommandFor(storageNode);
                nodeStartBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_START_RETRIES, taskCommand));
            }
            nodeStartBatch.addObserver(this);
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
                StopNode taskCommand = ecsInternalCommandFactory.createStopNodeCommandFor(storageNode);
                nodeStopBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_STOP_RETRIES, taskCommand));
            }
            nodeStopBatch.addObserver(this);
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
                ShutdownNode taskCommand = ecsInternalCommandFactory.createShutDownNodeCommandFor(storageNode);
                nodeShutdownBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES, taskCommand));
            }
            nodeShutdownBatch.addObserver(this);
            taskService.launchBatchTasks(nodeShutdownBatch);
            status = SHUTDOWNING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <shutdown> is not " +
                    "permitted." + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void addNode(int cacheSize, String displacementStrategy) throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> addNodeBatch = new BatchRetryableTasks(ADD_NODE);
            StorageNode newStorageNode = (StorageNode) ListUtils.getRandomObjectFrom(repository
                    .getNodesWithStatus(IDLE));
            List<StorageNode> nodes = ringTopology.getNodes();
            nodes.add(newStorageNode);
            RingTopology<StorageNode> newTopology = new RingTopology<>(RingMetadataHelper.computeRingOrder
                    (nodes));
            StorageNode successorNode = RingMetadataHelper.getSuccessorFrom(ringTopology,
                    newTopology, newStorageNode);
            updateRingMetadataFrom(ringTopology.updateTopologyWith(newTopology));

            LaunchJar taskCommand = ecsInternalCommandFactory
                    .createLaunchJarCommandWith(newStorageNode,
                            ExternalConfigurationServiceConstants.KV_SERVER_JAR_PATH, cacheSize,
                            displacementStrategy);

            List<AbstractCommand> successCommands = new ArrayList<>();
            successCommands.add(ecsInternalCommandFactory.createInitNodeMetadataCommandWith
                    (newStorageNode, ringMetadata));
            successCommands.add(ecsInternalCommandFactory.createSetWriteLockCommandFor
                    (successorNode));
            successCommands.add(ecsInternalCommandFactory.createInvokeDataTransferCommandWith
                    (successorNode, newStorageNode, ringMetadata));

            addNodeBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES,
                    taskCommand, successCommands));
            addNodeBatch.addObserver(this);
            taskService.launchBatchTasks(addNodeBatch);
            status = ADDING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <addNode> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    public void removeNode() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> removeBatch = new BatchRetryableTasks(REMOVE_NODE);
            StorageNode nodeToRemove = (StorageNode) ListUtils.getRandomObjectFrom(ringTopology.getNodes());
            nodeToRemove.setStatus(REMOVED);
            RingTopology<StorageNode> newTopology = new RingTopology<>(ringTopology);
            newTopology.removeNodes(nodeToRemove);

            StorageNode successorNode = RingMetadataHelper.getSuccessorFrom(ringTopology,
                    newTopology, nodeToRemove);
            updateRingMetadataFrom(ringTopology.updateTopologyWith(newTopology));

            SetWriteLock taskCommand = ecsInternalCommandFactory.createSetWriteLockCommandFor
                    (nodeToRemove);

            List<AbstractCommand> successCommands = new ArrayList<>();
            successCommands.add(ecsInternalCommandFactory.createUpdateMetadataCommandWith
                    (successorNode, ringMetadata));
            successCommands.add(ecsInternalCommandFactory.createInvokeDataTransferCommandWith
                    (nodeToRemove, successorNode, ringMetadata));

            removeBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES,
                    taskCommand, successCommands));
            removeBatch.addObserver(this);
            taskService.launchBatchTasks(removeBatch);
            status = REMOVING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <removeNode> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    private void initializeNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch = new
                BatchRetryableTasks(UPDATING_METADATA);

        for (StorageNode storageNode : repository.getNodesWithStatus(INITIALIZED)) {
            InitNodeMetadata taskCommand = ecsInternalCommandFactory
                    .createInitNodeMetadataCommandWith(storageNode, ringMetadata);

            nodeMetadataInitialisationBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
            nodeMetadataInitialisationBatch.addObserver(this);
        }

        taskService.launchBatchTasks(nodeMetadataInitialisationBatch);
        status = EcsStatus.UPDATING_METADATA;
    }

    private void updateNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataUpdateBatch = new
                BatchRetryableTasks(UPDATING_METADATA);

        for (StorageNode storageNode : ringTopology.getNodes()) {
            List<AbstractCommand> successCommands = new ArrayList<>();
            UpdateMetadata taskCommand = ecsInternalCommandFactory
                    .createUpdateMetadataCommandWith(storageNode, ringMetadata);
            if (storageNode.getStatus() == WRITELOCKED && status == ADDING_NODE) {
                successCommands.add(ecsInternalCommandFactory.createReleaseWriteLockCommandFor
                        (storageNode));
            } else if (storageNode.getStatus() == WRITELOCKED && status == REMOVING_NODE) {
                successCommands.add(ecsInternalCommandFactory.createShutDownNodeCommandFor(storageNode));
            }
            nodeMetadataUpdateBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES,
                            taskCommand, successCommands));
        }

        nodeMetadataUpdateBatch.addObserver(this);
        taskService.launchBatchTasks(nodeMetadataUpdateBatch);
        status = EcsStatus.UPDATING_METADATA;
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
        ringTopology.updateTopologyWith(ringOrderedNodes);
        updateRingMetadataFrom(ringTopology);
    }

    private void updateRingMetadataFrom(RingTopology<StorageNode> ringTopology) {
        HashRange previousRange = null;

        for (StorageNode node : ringTopology.getNodes()) {
            HashRange hashRange;
            int ringPosition = ringTopology.getRingPositionOf(node);

            hashRange = RingMetadataHelper.computeHashRangeForNodeBasedOnRingPosition(ringPosition,
                    ringTopology.getNumberOfNodes(), node.getHashKey(), previousRange);
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
                break;
            case START_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case STOP_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case REMOVE_NODE:
                updateNodesWithMetadata();
                break;
            case ADD_NODE:
                updateNodesWithMetadata();
                break;
            case SHUTDOWN:
                status = EcsStatus.INITIALIZED;
                break;
            case UPDATING_METADATA:
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
        private EcsInternalCommandFactory ecsInternalCommandFactory;

        public Builder ecsInternalCommandFactory(EcsInternalCommandFactory ecsInternalCommandFactory) {
            this.ecsInternalCommandFactory = ecsInternalCommandFactory;
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
