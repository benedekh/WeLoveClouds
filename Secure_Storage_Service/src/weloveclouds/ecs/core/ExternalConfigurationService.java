package weloveclouds.ecs.core;

import static weloveclouds.ecs.core.EcsStatus.ADDING_NODE;
import static weloveclouds.ecs.core.EcsStatus.INITIALIZING_SERVICE;
import static weloveclouds.ecs.core.EcsStatus.REMOVING_NODE;
import static weloveclouds.ecs.core.EcsStatus.SHUTDOWNING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STARTING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STOPPING_NODE;
import static weloveclouds.ecs.core.EcsStatus.UNINITIALIZED;
import static weloveclouds.ecs.core.EcsStatus.WAITING_FOR_SERVICE_INITIALIZATION;
import static weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage.Status.TOPOLOGY_UPDATE;
import static weloveclouds.ecs.models.repository.NodeStatus.IDLE;
import static weloveclouds.ecs.models.repository.NodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.NodeStatus.REMOVED;
import static weloveclouds.ecs.models.repository.NodeStatus.RUNNING;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.commons.utils.ListUtils;
import weloveclouds.ecs.contexts.EcsExecutionContext;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;
import weloveclouds.ecs.exceptions.configuration.InvalidConfigurationException;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.KVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.NotificationRequest;
import weloveclouds.ecs.models.repository.EcsRepository;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.ecs.models.stats.EcsStatistics;
import weloveclouds.ecs.models.tasks.AbstractBatchTasks;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.EcsBatchFactory;
import weloveclouds.ecs.models.tasks.details.AddNodeTaskDetails;
import weloveclouds.ecs.models.tasks.details.RemoveNodeTaskDetails;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.services.INotificationService;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.RingMetadataHelper;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfiguration;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService implements Observer {
    private static final Logger LOGGER = Logger.getLogger(ExternalConfigurationService.class);
    private static final IStatsdClient STATSD_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

    private EcsStatus status;
    private EcsRepository repository;
    private EcsRepositoryFactory ecsRepositoryFactory;
    private ITaskService taskService;
    private INotificationService<IKVEcsNotificationMessage> notificationService;
    private EcsBatchFactory ecsBatchFactory;
    private DistributedService distributedService;

    @Inject
    public ExternalConfigurationService(ITaskService taskService,
                                        EcsRepositoryFactory ecsRepositoryFactory,
                                        EcsBatchFactory ecsBatchFactory,
                                        INotificationService<IKVEcsNotificationMessage>
                                                notificationService,
                                        LoadBalancerConfiguration loadBalancerConfiguration)
            throws ServiceBootstrapException {
        this.status = UNINITIALIZED;
        this.taskService = taskService;
        this.notificationService = notificationService;
        this.ecsRepositoryFactory = ecsRepositoryFactory;
        this.ecsBatchFactory = ecsBatchFactory;
        this.distributedService = new DistributedService();

        bootstrapConfiguration(EcsExecutionContext.getConfigurationFilePath(),
                loadBalancerConfiguration);
        this.notificationService.start();
    }

    public void startLoadBalancer() throws ExternalConfigurationServiceException {
        if (status == UNINITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> loadBalancerStartBatch;
            loadBalancerStartBatch = ecsBatchFactory.createStartLoadBalancerBatchFor(repository
                    .getLoadbalancer());
            loadBalancerStartBatch.addObserver(this);
            taskService.launchBatchTasks(loadBalancerStartBatch);
        } else {
            throw new ExternalConfigurationServiceException("Operation <startLoadBalancer> is not "
                    + "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    @SuppressWarnings("unchecked")
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy)
            throws ExternalConfigurationServiceException {

        if (status == WAITING_FOR_SERVICE_INITIALIZATION) {
            try {
                AbstractBatchTasks<AbstractRetryableTask> serviceInitialisationBatch;

                List<StorageNode> storageNodesToInitialize =
                        (List<StorageNode>) ListUtils.getPreciseNumberOfRandomObjectsFrom(
                                repository.getNodesWithStatus(IDLE), numberOfNodes);

                serviceInitialisationBatch = ecsBatchFactory.createServiceInitialisationBatchWith(
                        repository.getLoadbalancer(), storageNodesToInitialize, cacheSize,
                        displacementStrategy);

                serviceInitialisationBatch.addObserver(this);
                taskService.launchBatchTasks(serviceInitialisationBatch);
                status = INITIALIZING_SERVICE;
            } catch (Exception e) {
                throw new ExternalConfigurationServiceException("Unable to initialise service " +
                        "with cause: " + e.getMessage());
            }
        } else {
            throw new ExternalConfigurationServiceException("Operation <initService> is not "
                    + "permitted. The external configuration service (ECS) is : " + status.name()
                    + ". Please execute the command <startLoadBalancer> first.");
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
            throw new ExternalConfigurationServiceException("Operation <start> is not permitted."
                    + " The external configuration service (ECS) is : " + status.name());
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
            throw new ExternalConfigurationServiceException("Operation <stop> is not permitted."
                    + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void shutDown() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            List<NodeStatus> activeNodeStatus = Arrays.asList(INITIALIZED, RUNNING);
            AbstractBatchTasks<AbstractRetryableTask> nodeShutdownBatch = ecsBatchFactory
                    .createShutdownNodeBatchFor(repository.getNodeWithStatus(activeNodeStatus));

            nodeShutdownBatch.addObserver(this);
            taskService.launchBatchTasks(nodeShutdownBatch);
            status = SHUTDOWNING_NODE;
        } else {
            throw new ExternalConfigurationServiceException(
                    "Operation <shutdown> is not " + "permitted."
                            + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void addNode(int cacheSize, String displacementStrategy)
            throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> addNodeBatch;
            StorageNode newStorageNode = (StorageNode) ListUtils
                    .getRandomObjectFrom(repository.getNodesWithStatus(IDLE));

            List<StorageNode> nodes = distributedService.getParticipatingNodes();
            nodes.add(newStorageNode);
            RingTopology<StorageNode> newTopology =
                    new RingTopology<>(RingMetadataHelper.computeRingOrder(nodes));

            StorageNode successorNode = RingMetadataHelper.getSuccessorFrom(
                    distributedService.getTopology(), newTopology, newStorageNode);
            distributedService.updateTopologyWith(newTopology);

            addNodeBatch = ecsBatchFactory
                    .createAddNodeBatchFrom(repository.getLoadbalancer(), new AddNodeTaskDetails(newStorageNode,
                            successorNode,
                            distributedService.getRingMetadata(), displacementStrategy, cacheSize));

            addNodeBatch.addObserver(this);
            taskService.launchBatchTasks(addNodeBatch);
            status = ADDING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <addNode> is not "
                    + "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    public void removeNode() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchTasks<AbstractRetryableTask> removeBatch;
            StorageNode nodeToRemove = (StorageNode) ListUtils
                    .getRandomObjectFrom(distributedService.getParticipatingNodes());
            nodeToRemove.setStatus(REMOVED);

            RingTopology<StorageNode> newTopology =
                    new RingTopology<>(distributedService.getTopology());
            newTopology.removeNodes(nodeToRemove);

            StorageNode successorNode = RingMetadataHelper
                    .getSuccessorFrom(distributedService.getTopology(), newTopology, nodeToRemove);
            distributedService.updateTopologyWith(newTopology);

            removeBatch = ecsBatchFactory.createRemoveNodeBatchFrom(new RemoveNodeTaskDetails(
                    nodeToRemove, successorNode, distributedService.getRingMetadata()));

            removeBatch.addObserver(this);
            taskService.launchBatchTasks(removeBatch);
            status = REMOVING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <removeNode> is not "
                    + "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    private void initializeNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch =
                ecsBatchFactory.createNodeMetadataInitialisationBatchWith(
                        repository.getNodesWithStatus(INITIALIZED), distributedService);

        nodeMetadataInitialisationBatch.addObserver(this);
        taskService.launchBatchTasks(nodeMetadataInitialisationBatch);
        status = EcsStatus.UPDATING_METADATA;

        notifyLoadBalancerForTopologyChanges();
    }

    private void updateNodesWithMetadata() {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataUpdateBatch = ecsBatchFactory
                .createNodeMetadataUpdateBatchWith(distributedService.getParticipatingNodes(),
                        distributedService.getRingMetadata(), status);

        nodeMetadataUpdateBatch.addObserver(this);
        taskService.launchBatchTasks(nodeMetadataUpdateBatch);
        status = EcsStatus.UPDATING_METADATA;

        notifyLoadBalancerForTopologyChanges();
    }

    @SuppressWarnings("unchecked")
    private void notifyLoadBalancerForTopologyChanges() {
        notificationService.process(new NotificationRequest.Builder<IKVEcsNotificationMessage>()
                .target(repository.getLoadbalancer())
                .notificationMessage(new KVEcsNotificationMessage.Builder()
                        .status(TOPOLOGY_UPDATE)
                        .ringTopology(distributedService.getTopology()).build())
                .build());
    }

    public EcsStatistics getStats() {
        return new EcsStatistics.Builder()
                .status(status)
                .loadBalancer(repository.getLoadbalancer())
                .initializedNodes(repository.getNodesWithStatus(INITIALIZED))
                .idledNodes(repository.getNodesWithStatus(IDLE))
                .runningNodes(repository.getNodesWithStatus(RUNNING))
                .build();
    }

    private void bootstrapConfiguration(String ecsConfigurationFilePath, LoadBalancerConfiguration
            loadBalancerConfiguration) throws ServiceBootstrapException {
        try {
            repository =
                    ecsRepositoryFactory.createEcsRepositoryFrom(new File(ecsConfigurationFilePath),
                            loadBalancerConfiguration);
        } catch (InvalidConfigurationException ex) {
            throw new ServiceBootstrapException(
                    "Bootstrap failed. Unable to start the service : " + ex.getMessage(), ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable obs, Object obj) {
        AbstractBatchTasks<AbstractRetryableTask> batch =
                (AbstractBatchTasks<AbstractRetryableTask>) obs;
        List<AbstractRetryableTask> failedTasks = (List<AbstractRetryableTask>) obj;

        if (failedTasks.isEmpty()) {
            displayToUser(StringUtils.join(" ", batch, "Ended successfully."));
        } else {
            displayToUser(StringUtils.join(" ", batch, "Ended with", failedTasks.size(),
                    "failed tasks."));
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
            case START_LOAD_BALANCER:
                if (!batch.hasFailed()) {
                    status = EcsStatus.WAITING_FOR_SERVICE_INITIALIZATION;
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
            // Log
        }
    }
}
