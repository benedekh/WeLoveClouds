package weloveclouds.ecs.core;

import static weloveclouds.ecs.core.EcsStatus.ADDING_NODE;
import static weloveclouds.ecs.core.EcsStatus.INITIALIZING_SERVICE;
import static weloveclouds.ecs.core.EcsStatus.REMOVING_NODE;
import static weloveclouds.ecs.core.EcsStatus.SHUTDOWNING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STARTING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STOPPING_NODE;
import static weloveclouds.ecs.core.EcsStatus.UNINITIALIZED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.IDLE;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.REMOVED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.RUNNING;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;
import weloveclouds.commons.utils.ListUtils;
import weloveclouds.ecs.contexts.EcsExecutionContext;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;
import weloveclouds.ecs.exceptions.InvalidConfigurationException;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
import weloveclouds.ecs.models.repository.EcsRepository;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.repository.StorageNodeStatus;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.ecs.models.tasks.AbstractBatchTasks;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.EcsBatchFactory;
import weloveclouds.ecs.models.tasks.details.AddNodeTaskDetails;
import weloveclouds.ecs.models.tasks.details.RemoveNodeTaskDetails;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.RingMetadataHelper;


/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService implements Observer {
    private static final Logger LOGGER = Logger.getLogger(ExternalConfigurationService.class);
    private static final IStatsdClient STATSD_CLIENT = StatsdClientFactory
            .createStatdClientFromEnvironment();

    private EcsStatus status;
    private final HashRange INITIAL_HASHRANGE;
    private String configurationFilePath;
    private EcsRepository repository;
    private EcsRepositoryFactory ecsRepositoryFactory;
    private ITaskService taskService;
    private EcsBatchFactory ecsBatchFactory;
    private DistributedService distributedService;

    @Inject
    public ExternalConfigurationService(ITaskService taskService,
                                        EcsRepositoryFactory ecsRepositoryFactory,
                                        EcsBatchFactory ecsBatchFactory) throws ServiceBootstrapException {
        this.taskService = taskService;
        this.ecsRepositoryFactory = ecsRepositoryFactory;
        this.configurationFilePath = EcsExecutionContext.getConfigurationFilePath();
        this.ecsBatchFactory = ecsBatchFactory;
        INITIAL_HASHRANGE = new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE)
                .build();
        bootstrapConfiguration();
        this.status = UNINITIALIZED;
        this.distributedService = new DistributedService();
    }

    @SuppressWarnings("unchecked")
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) throws
            ExternalConfigurationServiceException {
        if (status == UNINITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> nodeInitialisationBatch;
            List<StorageNode> storageNodesToInitialize = (List<StorageNode>) ListUtils
                    .getPreciseNumberOfRandomObjectsFrom(repository.getNodesWithStatus(IDLE), numberOfNodes);

            nodeInitialisationBatch = ecsBatchFactory.createInitNodeMetadataBatchWith
                    (storageNodesToInitialize, cacheSize, displacementStrategy);

            nodeInitialisationBatch.addObserver(this);
            taskService.launchBatchTasks(nodeInitialisationBatch);
            status = INITIALIZING_SERVICE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <initService> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    public void start() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> nodeStartBatch = ecsBatchFactory
                    .createStartNodeBatchFor(distributedService.getParticipatingNodes());

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
            AbstractBatchTasks<AbstractRetryableTask> nodeStopBatch = ecsBatchFactory
                    .createStopNodeBatchFor(distributedService.getParticipatingNodes());

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
            List<StorageNodeStatus> activeNodeStatus = Arrays.asList(INITIALIZED, RUNNING);
            AbstractBatchTasks<AbstractRetryableTask> nodeShutdownBatch = ecsBatchFactory
                    .createShutdownNodeBatchFor(repository.getNodeWithStatus(activeNodeStatus));

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
            AbstractBatchTasks<AbstractRetryableTask> addNodeBatch;
            StorageNode newStorageNode = (StorageNode) ListUtils.getRandomObjectFrom(repository
                    .getNodesWithStatus(IDLE));

            List<StorageNode> nodes = distributedService.getParticipatingNodes();
            nodes.add(newStorageNode);
            RingTopology<StorageNode> newTopology = new RingTopology<>(RingMetadataHelper.computeRingOrder
                    (nodes));

            StorageNode successorNode = RingMetadataHelper.getSuccessorFrom(distributedService.getTopology(),
                    newTopology, newStorageNode);
            distributedService.updateTopologyWith(newTopology);

            addNodeBatch = ecsBatchFactory.createAddNodeBatchFrom(new AddNodeTaskDetails
                    (newStorageNode, successorNode, distributedService.getRingMetadata(), displacementStrategy,
                            cacheSize));

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
            AbstractBatchTasks<AbstractRetryableTask> removeBatch;
            StorageNode nodeToRemove = (StorageNode) ListUtils.getRandomObjectFrom
                    (distributedService.getParticipatingNodes());
            nodeToRemove.setStatus(REMOVED);

            RingTopology<StorageNode> newTopology = new RingTopology<>(distributedService.getTopology());
            newTopology.removeNodes(nodeToRemove);

            StorageNode successorNode = RingMetadataHelper.getSuccessorFrom(distributedService.getTopology(),
                    newTopology, nodeToRemove);
            distributedService.updateTopologyWith(newTopology);

            removeBatch = ecsBatchFactory.createRemoveNodeBatchFrom(new RemoveNodeTaskDetails
                    (nodeToRemove, successorNode, distributedService.getRingMetadata()));

            removeBatch.addObserver(this);
            taskService.launchBatchTasks(removeBatch);
            status = REMOVING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <removeNode> is not " +
                    "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    private void initializeNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch =
                ecsBatchFactory.createNodeMetadataInitialisationBatchWith(repository
                        .getNodesWithStatus(INITIALIZED), distributedService.getRingMetadata());

        nodeMetadataInitialisationBatch.addObserver(this);
        taskService.launchBatchTasks(nodeMetadataInitialisationBatch);
        status = EcsStatus.UPDATING_METADATA;
    }

    private void updateNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataUpdateBatch = ecsBatchFactory
                .createNodeMetadataUpdateBatchWith(distributedService.getParticipatingNodes(),
                        distributedService.getRingMetadata(), status);

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
                if (!batch.hasFailed()) {
                    distributedService.initializeWith(repository.getNodesWithStatus(INITIALIZED));
                    initializeNodesWithMetadata();
                } else {
                    status = EcsStatus.UNINITIALIZED;
                }
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
                status = EcsStatus.UNINITIALIZED;
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
}
