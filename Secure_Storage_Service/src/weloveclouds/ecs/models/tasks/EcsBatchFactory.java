package weloveclouds.ecs.models.tasks;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.ecs.core.EcsStatus;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.internal.EcsInternalCommandFactory;
import weloveclouds.ecs.models.commands.internal.InitNodeMetadata;
import weloveclouds.ecs.models.commands.internal.SetWriteLock;
import weloveclouds.ecs.models.commands.internal.ShutdownNode;
import weloveclouds.ecs.models.commands.internal.StartNode;
import weloveclouds.ecs.models.commands.internal.StopNode;
import weloveclouds.ecs.models.commands.internal.UpdateMetadata;
import weloveclouds.ecs.models.commands.internal.ssh.LaunchJar;
import weloveclouds.ecs.models.repository.LoadBalancer;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.ecs.models.tasks.details.AddNodeTaskDetails;
import weloveclouds.ecs.models.tasks.details.RemoveNodeTaskDetails;
import weloveclouds.commons.hashing.models.RingMetadata;

import static weloveclouds.ecs.core.EcsStatus.ADDING_NODE;
import static weloveclouds.ecs.core.EcsStatus.REMOVING_NODE;
import static weloveclouds.ecs.core.ExternalConfigurationServiceConstants.MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES;
import static weloveclouds.ecs.core.ExternalConfigurationServiceConstants.MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES;
import static weloveclouds.ecs.core.ExternalConfigurationServiceConstants.MAX_NUMBER_OF_NODE_START_RETRIES;
import static weloveclouds.ecs.core.ExternalConfigurationServiceConstants.MAX_NUMBER_OF_NODE_STOP_RETRIES;
import static weloveclouds.ecs.models.repository.NodeStatus.WRITE_LOCKED;
import static weloveclouds.ecs.models.tasks.BatchPurpose.ADD_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.REMOVE_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.SERVICE_INITIALISATION;
import static weloveclouds.ecs.models.tasks.BatchPurpose.SHUTDOWN;
import static weloveclouds.ecs.models.tasks.BatchPurpose.START_LOAD_BALANCER;
import static weloveclouds.ecs.models.tasks.BatchPurpose.START_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.STOP_NODE;
import static weloveclouds.ecs.models.tasks.BatchPurpose.UPDATING_METADATA;

/**
 * Created by Benoit on 2016-11-30.
 */
public class EcsBatchFactory {
    EcsInternalCommandFactory ecsInternalCommandFactory;

    @Inject
    public EcsBatchFactory(EcsInternalCommandFactory ecsInternalCommandFactory) {
        this.ecsInternalCommandFactory = ecsInternalCommandFactory;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createServiceInitialisationBatchWith(
            LoadBalancer loadbalancer, List<StorageNode> nodesToInitialize, int cacheSize, String
            displacementStrategy) {
        AbstractBatchTasks<AbstractRetryableTask> serviceInitialisationBatch = new
                BatchRetryableTasks(SERVICE_INITIALISATION);

        for (StorageNode storageNode : nodesToInitialize) {
            AbstractCommand taskCommand = ecsInternalCommandFactory
                    .createLaunchStorageNodesJarsCommandWith(
                            loadbalancer,
                            storageNode,
                            ExternalConfigurationServiceConstants.KV_SERVER_JAR_PATH,
                            cacheSize,
                            displacementStrategy);
            serviceInitialisationBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
        }

        return serviceInitialisationBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createStartLoadBalancerBatchFor(LoadBalancer
                                                                                             loadBalancer) {
        AbstractBatchTasks<AbstractRetryableTask> startLoadBalancerBatch = new
                BatchRetryableTasks(START_LOAD_BALANCER);
        AbstractCommand loadBalancerInitialisation = ecsInternalCommandFactory
                .createLaunchLoadBalancerJarCommandWith(loadBalancer,
                        ExternalConfigurationServiceConstants.LB_SERVER_JAR_PATH);
        startLoadBalancerBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES,
                loadBalancerInitialisation));
        return startLoadBalancerBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createStartNodeBatchFor(List<StorageNode>
                                                                                     nodesToStart) {
        AbstractBatchTasks<AbstractRetryableTask> nodeStartBatch = new BatchRetryableTasks(START_NODE);

        for (StorageNode storageNode : nodesToStart) {
            StartNode taskCommand = ecsInternalCommandFactory.createStartNodeCommandFor(storageNode);
            nodeStartBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_START_RETRIES, taskCommand));
        }

        return nodeStartBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createStopNodeBatchFor(List<StorageNode> nodesToStop) {
        AbstractBatchTasks<AbstractRetryableTask> nodeStopBatch = new BatchRetryableTasks(STOP_NODE);

        for (StorageNode storageNode : nodesToStop) {
            StopNode taskCommand = ecsInternalCommandFactory.createStopNodeCommandFor(storageNode);
            nodeStopBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_STOP_RETRIES, taskCommand));
        }

        return nodeStopBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createShutdownNodeBatchFor
            (List<StorageNode> nodesToShutdown) {
        AbstractBatchTasks<AbstractRetryableTask> nodeShutdownBatch = new BatchRetryableTasks(SHUTDOWN);

        for (StorageNode storageNode : nodesToShutdown) {
            ShutdownNode taskCommand = ecsInternalCommandFactory.createShutDownNodeCommandFor(storageNode);
            nodeShutdownBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES, taskCommand));
        }
        return nodeShutdownBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createAddNodeBatchFrom(LoadBalancer
                                                                                    loadbalancer,
                                                                            AddNodeTaskDetails
                                                                                    addNodeTaskDetails) {
        AbstractBatchTasks<AbstractRetryableTask> addNodeBatch = new BatchRetryableTasks(ADD_NODE);

        LaunchJar taskCommand = ecsInternalCommandFactory
                .createLaunchStorageNodesJarsCommandWith(loadbalancer, addNodeTaskDetails
                                .getNewStorageNode(),
                        ExternalConfigurationServiceConstants.KV_SERVER_JAR_PATH,
                        addNodeTaskDetails.getCacheSize(),
                        addNodeTaskDetails.getDisplacementStrategy());

        List<AbstractCommand> successCommands = new ArrayList<>();
        successCommands.add(ecsInternalCommandFactory.createInitNodeMetadataCommandWith
                (addNodeTaskDetails.getNewStorageNode(), addNodeTaskDetails.getRingMetadata()));
        successCommands.add(ecsInternalCommandFactory.createSetWriteLockCommandFor
                (addNodeTaskDetails.getSuccessor()));
        successCommands.add(ecsInternalCommandFactory.createInvokeDataTransferCommandWith
                (addNodeTaskDetails.getSuccessor(), addNodeTaskDetails.getNewStorageNode(),
                        addNodeTaskDetails.getRingMetadata()));

        addNodeBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES,
                taskCommand, successCommands));

        return addNodeBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createRemoveNodeBatchFrom
            (RemoveNodeTaskDetails removeNodeTaskDetails) {
        AbstractBatchTasks<AbstractRetryableTask> removeBatch = new BatchRetryableTasks(REMOVE_NODE);

        SetWriteLock taskCommand = ecsInternalCommandFactory.createSetWriteLockCommandFor
                (removeNodeTaskDetails.getNodetoRemove());

        List<AbstractCommand> successCommands = new ArrayList<>();
        successCommands.add(ecsInternalCommandFactory.createUpdateMetadataCommandWith
                (removeNodeTaskDetails.getSuccessor(), removeNodeTaskDetails.getRingMetadata()));
        successCommands.add(ecsInternalCommandFactory.createInvokeDataTransferCommandWith
                (removeNodeTaskDetails.getNodetoRemove(), removeNodeTaskDetails.getSuccessor(),
                        removeNodeTaskDetails.getRingMetadata()));

        removeBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES,
                taskCommand, successCommands));

        return removeBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createNodeMetadataInitialisationBatchWith
            (List<StorageNode> nodesToInitialize, DistributedService distributedService) {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch = new
                BatchRetryableTasks(UPDATING_METADATA);

        for (StorageNode storageNode : nodesToInitialize) {
            InitNodeMetadata taskCommand = ecsInternalCommandFactory
                    .createInitNodeMetadataCommandWith(storageNode, distributedService.getRingMetadata());

            nodeMetadataInitialisationBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
        }
        return nodeMetadataInitialisationBatch;
    }

    public AbstractBatchTasks<AbstractRetryableTask> createNodeMetadataUpdateBatchWith
            (List<StorageNode> nodesToUpdate, RingMetadata ringMetadata, EcsStatus ecsStatus) {
        AbstractBatchTasks<AbstractRetryableTask> nodeMetadataUpdateBatch = new
                BatchRetryableTasks(UPDATING_METADATA);

        for (StorageNode storageNode : nodesToUpdate) {
            List<AbstractCommand> successCommands = new ArrayList<>();
            UpdateMetadata taskCommand = ecsInternalCommandFactory
                    .createUpdateMetadataCommandWith(storageNode, ringMetadata);
            if (storageNode.getStatus() == WRITE_LOCKED && ecsStatus == ADDING_NODE) {
                successCommands.add(ecsInternalCommandFactory.createReleaseWriteLockCommandFor
                        (storageNode));
            } else if (storageNode.getStatus() == WRITE_LOCKED && ecsStatus == REMOVING_NODE) {
                successCommands.add(ecsInternalCommandFactory.createShutDownNodeCommandFor(storageNode));
            }
            nodeMetadataUpdateBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES,
                            taskCommand, successCommands));
        }

        return nodeMetadataUpdateBatch;
    }
}
