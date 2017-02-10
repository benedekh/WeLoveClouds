package weloveclouds.ecs.core;

import static weloveclouds.ecs.core.EcsStatus.ADDING_NODE;
import static weloveclouds.ecs.core.EcsStatus.INITIALIZING_LOAD_BALANCER;
import static weloveclouds.ecs.core.EcsStatus.INITIALIZING_SERVICE;
import static weloveclouds.ecs.core.EcsStatus.REMOVING_NODE;
import static weloveclouds.ecs.core.EcsStatus.SHUTTING_DOWN_NODE;
import static weloveclouds.ecs.core.EcsStatus.STARTING_NODE;
import static weloveclouds.ecs.core.EcsStatus.STOPPING_NODE;
import static weloveclouds.ecs.core.EcsStatus.WAITING_FOR_LOAD_BALANCER_INITIALIZATION;
import static weloveclouds.ecs.core.EcsStatus.WAITING_FOR_SERVICE_INITIALIZATION;
import static weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage.Status.TOPOLOGY_UPDATE;
import static weloveclouds.ecs.models.repository.NodeStatus.ERROR;
import static weloveclouds.ecs.models.repository.NodeStatus.IDLE;
import static weloveclouds.ecs.models.repository.NodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.NodeStatus.REMOVED;
import static weloveclouds.ecs.models.repository.NodeStatus.RUNNING;
import static weloveclouds.ecs.models.repository.NodeStatus.UNSYNCHRONIZED;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

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
import weloveclouds.ecs.models.stats.EcsStatistics;
import weloveclouds.ecs.models.tasks.AbstractBatchOfTasks;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.EcsBatchFactory;
import weloveclouds.ecs.models.tasks.details.AddNodeTaskDetails;
import weloveclouds.ecs.models.tasks.details.RemoveNodeTaskDetails;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.services.INotificationService;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.RingMetadataHelper;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfiguration;
import weloveclouds.loadbalancer.services.IDistributedSystemAccessService;

/**
 * Created by Benoit on 2016-11-16.
 */
@Singleton
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
    private IDistributedSystemAccessService distributedServiceAccess;

    @Inject
    public ExternalConfigurationService(ITaskService taskService,
                                        EcsRepositoryFactory ecsRepositoryFactory,
                                        EcsBatchFactory ecsBatchFactory,
                                        INotificationService<IKVEcsNotificationMessage>
                                                notificationService,
                                        IDistributedSystemAccessService distributedServiceAccess,
                                        LoadBalancerConfiguration loadBalancerConfiguration)
            throws ServiceBootstrapException {
        this.status = WAITING_FOR_LOAD_BALANCER_INITIALIZATION;
        this.taskService = taskService;
        this.notificationService = notificationService;
        this.ecsRepositoryFactory = ecsRepositoryFactory;
        this.ecsBatchFactory = ecsBatchFactory;
        this.distributedServiceAccess = distributedServiceAccess;

        bootstrapConfiguration(EcsExecutionContext.getConfigurationFilePath(),
                loadBalancerConfiguration);
        this.notificationService.start();
    }

    public EcsStatus getStatus() {
        return status;
    }

    public EcsRepository getRepository() {
        return repository;
    }

    public void startLoadBalancer() throws ExternalConfigurationServiceException {
        if (status == WAITING_FOR_LOAD_BALANCER_INITIALIZATION) {
            status = INITIALIZING_LOAD_BALANCER;
            AbstractBatchOfTasks<AbstractRetryableTask> loadBalancerStartBatch;
            loadBalancerStartBatch = ecsBatchFactory.createStartLoadBalancerBatchFor(repository
                    .getLoadbalancer());
            loadBalancerStartBatch.addObserver(this);
            taskService.launchBatch(loadBalancerStartBatch);
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
                AbstractBatchOfTasks<AbstractRetryableTask> serviceInitialisationBatch;

                List<StorageNode> storageNodesToInitialize =
                        (List<StorageNode>) ListUtils.getPreciseNumberOfRandomObjectsFrom(
                                repository.getNodesWithStatus(IDLE), numberOfNodes);

                serviceInitialisationBatch = ecsBatchFactory.createServiceInitialisationBatchWith(
                        repository.getLoadbalancer(), storageNodesToInitialize, cacheSize,
                        displacementStrategy);

                serviceInitialisationBatch.addObserver(this);
                taskService.launchBatch(serviceInitialisationBatch);
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
            AbstractBatchOfTasks<AbstractRetryableTask> nodeStartBatch = ecsBatchFactory
                    .createStartNodeBatchFor(repository.getNodesWithStatus(INITIALIZED));

            nodeStartBatch.addObserver(this);
            taskService.launchBatch(nodeStartBatch);
            status = STARTING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <start> is not permitted."
                    + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void stop() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchOfTasks<AbstractRetryableTask> nodeStopBatch = ecsBatchFactory
                    .createStopNodeBatchFor(distributedServiceAccess.getParticipatingNodes());

            nodeStopBatch.addObserver(this);
            taskService.launchBatch(nodeStopBatch);
            status = STOPPING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <stop> is not permitted."
                    + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void shutDown() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            List<NodeStatus> activeNodeStatus = Arrays.asList(INITIALIZED, RUNNING);
            AbstractBatchOfTasks<AbstractRetryableTask> nodeShutdownBatch = ecsBatchFactory
                    .createShutdownNodeBatchFor(repository.getNodeWithStatus(activeNodeStatus));

            nodeShutdownBatch.addObserver(this);
            taskService.launchBatch(nodeShutdownBatch);
            status = SHUTTING_DOWN_NODE;
        } else {
            throw new ExternalConfigurationServiceException(
                    "Operation <shutdown> is not " + "permitted."
                            + " The external configuration service (ECS) is : " + status.name());
        }
    }

    public void addNode(int cacheSize, String displacementStrategy, boolean withNodeAutomaticStart)
            throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchOfTasks<AbstractRetryableTask> addNodeBatch;
            StorageNode newStorageNode = (StorageNode) ListUtils
                    .getRandomObjectFrom(repository.getNodesWithStatus(IDLE));

            List<StorageNode> nodes = distributedServiceAccess.getParticipatingNodes();
            nodes.add(newStorageNode);
            RingTopology<StorageNode> newTopology =
                    new RingTopology<>(RingMetadataHelper.computeRingOrder(nodes));

            StorageNode successorNode = RingMetadataHelper.getSuccessorFrom(
                    distributedServiceAccess.getTopology(), newTopology, newStorageNode);
            distributedServiceAccess.updateServiceTopologyWith(newTopology);

            addNodeBatch = ecsBatchFactory
                    .createAddNodeBatchFrom(repository.getLoadbalancer(), new AddNodeTaskDetails(newStorageNode,
                            successorNode,
                            distributedServiceAccess.getRingMetadata(), displacementStrategy,
                            cacheSize), withNodeAutomaticStart);

            addNodeBatch.addObserver(this);
            taskService.launchBatch(addNodeBatch);
            status = ADDING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <addNode> is not "
                    + "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    public void removeNode() throws ExternalConfigurationServiceException {
        if (status == EcsStatus.INITIALIZED) {
            AbstractBatchOfTasks<AbstractRetryableTask> removeBatch;
            StorageNode nodeToRemove = (StorageNode) ListUtils
                    .getRandomObjectFrom(distributedServiceAccess.getParticipatingNodes());
            nodeToRemove.setStatus(REMOVED);

            RingTopology<StorageNode> newTopology =
                    new RingTopology<>(distributedServiceAccess.getTopology());
            newTopology.removeNodes(nodeToRemove);

            StorageNode successorNode = RingMetadataHelper
                    .getSuccessorFrom(distributedServiceAccess.getTopology(), newTopology, nodeToRemove);
            distributedServiceAccess.updateServiceTopologyWith(newTopology);

            removeBatch = ecsBatchFactory.createRemoveNodeBatchFrom(new RemoveNodeTaskDetails(
                    nodeToRemove, successorNode, distributedServiceAccess.getRingMetadata()));

            removeBatch.addObserver(this);
            taskService.launchBatch(removeBatch);
            status = REMOVING_NODE;
        } else {
            throw new ExternalConfigurationServiceException("Operation <removeNode> is not "
                    + "permitted. The external configuration service (ECS) is : " + status.name());
        }
    }

    public void removeUnresponsiveNodesWithName(String nodeName) throws ExternalConfigurationServiceException {
        StorageNode unresponsiveNode = distributedServiceAccess.getNodeFrom(nodeName);
        if (unresponsiveNode.getStatus() != IDLE) {
            unresponsiveNode.setStatus(ERROR);
            unresponsiveNode.setMetadataStatus(UNSYNCHRONIZED);
            unresponsiveNode.clearHashRange();
            distributedServiceAccess.removeParticipatingNode(unresponsiveNode);
            addNode(200, "LFU", true);
        }
    }

    private void initializeNodesWithMetadata() {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch =
                ecsBatchFactory.createNodeMetadataInitialisationBatchWith(
                        repository.getNodesWithStatus(INITIALIZED), distributedServiceAccess);

        nodeMetadataInitialisationBatch.addObserver(this);
        taskService.launchBatch(nodeMetadataInitialisationBatch);
        status = EcsStatus.UPDATING_METADATA;

        notifyLoadBalancerForTopologyChanges();
    }

    private void updateNodesWithMetadata() {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeMetadataUpdateBatch = ecsBatchFactory
                .createNodeMetadataUpdateBatchWith(distributedServiceAccess.getParticipatingNodes(),
                        distributedServiceAccess.getRingMetadata(), status);

        nodeMetadataUpdateBatch.addObserver(this);
        taskService.launchBatch(nodeMetadataUpdateBatch);
        status = EcsStatus.UPDATING_METADATA;

        notifyLoadBalancerForTopologyChanges();
    }

    @SuppressWarnings("unchecked")
    private void notifyLoadBalancerForTopologyChanges() {
        notificationService.process(new NotificationRequest.Builder<IKVEcsNotificationMessage>()
                .target(repository.getLoadbalancer())
                .notificationMessage(new KVEcsNotificationMessage.Builder()
                        .status(TOPOLOGY_UPDATE)
                        .ringTopology(distributedServiceAccess.getTopology()).build())
                .build());
    }

    public EcsStatistics getStats() {
        return new EcsStatistics.Builder()
                .status(status)
                .loadBalancer(repository.getLoadbalancer())
                .initializedNodes(repository.getNodesWithStatus(INITIALIZED))
                .idledNodes(repository.getNodesWithStatus(IDLE))
                .runningNodes(repository.getNodesWithStatus(RUNNING))
                .errorNodes(repository.getNodesWithStatus(ERROR))
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
        AbstractBatchOfTasks<AbstractRetryableTask> batch =
                (AbstractBatchOfTasks<AbstractRetryableTask>) obs;
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
                    distributedServiceAccess.initializeServiceWith(repository.getNodesWithStatus(INITIALIZED));
                    initializeNodesWithMetadata();
                } else {
                    status = EcsStatus.WAITING_FOR_SERVICE_INITIALIZATION;
                }
                break;
            case START_LOAD_BALANCER:
                if (!batch.hasFailed()) {
                    status = EcsStatus.WAITING_FOR_SERVICE_INITIALIZATION;
                } else {
                    status = EcsStatus.WAITING_FOR_LOAD_BALANCER_INITIALIZATION;
                }
                break;
            case START_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case STOP_NODE:
                status = EcsStatus.INITIALIZED;
                break;
            case REMOVE_NODE:
                if (!batch.hasFailed()) {
                    for (StorageNode node : distributedServiceAccess.getParticipatingNodes()) {
                        node.setMetadataStatus(UNSYNCHRONIZED);
                    }
                    updateNodesWithMetadata();
                } else {
                    status = EcsStatus.INITIALIZED;
                }
                break;
            case ADD_NODE:
                if (!batch.hasFailed()) {
                    for (StorageNode node : distributedServiceAccess.getParticipatingNodes()) {
                        node.setMetadataStatus(UNSYNCHRONIZED);
                    }
                    updateNodesWithMetadata();
                } else {
                    status = EcsStatus.INITIALIZED;
                }
                break;
            case SHUTDOWN:
                if (!batch.hasFailed()) {
                    for (StorageNode node : distributedServiceAccess.getParticipatingNodes()) {
                        node.setMetadataStatus(UNSYNCHRONIZED);
                    }
                    status = EcsStatus.WAITING_FOR_SERVICE_INITIALIZATION;
                } else {
                    status = EcsStatus.INITIALIZED;
                }
                break;
            case UPDATING_METADATA:
                if (!batch.hasFailed()) {
                    status = EcsStatus.INITIALIZED;
                } else {
                    status = WAITING_FOR_SERVICE_INITIALIZATION;
                }
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
