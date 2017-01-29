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
import weloveclouds.ecs.models.tasks.details.AddNodeTaskDetails;
import weloveclouds.ecs.models.tasks.details.RemoveNodeTaskDetails;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.loadbalancer.services.IDistributedSystemAccessService;

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

    public AbstractBatchOfTasks<AbstractRetryableTask> createServiceInitialisationBatchWith(
            LoadBalancer loadbalancer, List<StorageNode> nodesToInitialize, int cacheSize, String
            displacementStrategy) {
        AbstractBatchOfTasks<AbstractRetryableTask> serviceInitialisationBatch = new
                BatchOfRetryableTasks(SERVICE_INITIALISATION);

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

    public AbstractBatchOfTasks<AbstractRetryableTask> createStartLoadBalancerBatchFor(LoadBalancer
                                                                                             loadBalancer) {
        AbstractBatchOfTasks<AbstractRetryableTask> startLoadBalancerBatch = new
                BatchOfRetryableTasks(START_LOAD_BALANCER);
        AbstractCommand loadBalancerInitialisation = ecsInternalCommandFactory
                .createLaunchLoadBalancerJarCommandWith(loadBalancer,
                        ExternalConfigurationServiceConstants.LB_SERVER_JAR_PATH);
        startLoadBalancerBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES,
                loadBalancerInitialisation));
        return startLoadBalancerBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createStartNodeBatchFor(List<StorageNode>
                                                                                     nodesToStart) {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeStartBatch = new BatchOfRetryableTasks(START_NODE);

        for (StorageNode storageNode : nodesToStart) {
            StartNode taskCommand = ecsInternalCommandFactory.createStartNodeCommandFor(storageNode);
            nodeStartBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_START_RETRIES, taskCommand));
        }

        return nodeStartBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createStopNodeBatchFor(List<StorageNode> nodesToStop) {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeStopBatch = new BatchOfRetryableTasks(STOP_NODE);

        for (StorageNode storageNode : nodesToStop) {
            StopNode taskCommand = ecsInternalCommandFactory.createStopNodeCommandFor(storageNode);
            nodeStopBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_STOP_RETRIES, taskCommand));
        }

        return nodeStopBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createShutdownNodeBatchFor
            (List<StorageNode> nodesToShutdown) {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeShutdownBatch = new BatchOfRetryableTasks(SHUTDOWN);

        for (StorageNode storageNode : nodesToShutdown) {
            ShutdownNode taskCommand = ecsInternalCommandFactory.createShutDownNodeCommandFor(storageNode);
            nodeShutdownBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES, taskCommand));
        }
        return nodeShutdownBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createAddNodeBatchFrom(LoadBalancer loadbalancer, AddNodeTaskDetails
            addNodeTaskDetails, boolean withAutomaticStart) {
        AbstractBatchOfTasks<AbstractRetryableTask> addNodeBatch = new BatchOfRetryableTasks(ADD_NODE);

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
                        addNodeTaskDetails.getRingMetadata(),false));

        if (withAutomaticStart) {
            successCommands.add(ecsInternalCommandFactory.createStartNodeCommandFor
                    (addNodeTaskDetails.getNewStorageNode()));
        }

        addNodeBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES,
                taskCommand, successCommands));

        return addNodeBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createRemoveNodeBatchFrom
            (RemoveNodeTaskDetails removeNodeTaskDetails) {
        AbstractBatchOfTasks<AbstractRetryableTask> removeBatch = new BatchOfRetryableTasks(REMOVE_NODE);

        SetWriteLock taskCommand = ecsInternalCommandFactory.createSetWriteLockCommandFor
                (removeNodeTaskDetails.getNodetoRemove());

        List<AbstractCommand> successCommands = new ArrayList<>();
        successCommands.add(ecsInternalCommandFactory.createUpdateMetadataCommandWith
                (removeNodeTaskDetails.getSuccessor(), removeNodeTaskDetails.getRingMetadata()));
        successCommands.add(ecsInternalCommandFactory.createInvokeDataTransferCommandWith
                (removeNodeTaskDetails.getNodetoRemove(), removeNodeTaskDetails.getSuccessor(),
                        removeNodeTaskDetails.getRingMetadata(),true));

        removeBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES,
                taskCommand, successCommands));

        return removeBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createNodeMetadataInitialisationBatchWith
            (List<StorageNode> nodesToInitialize, IDistributedSystemAccessService
                    distributedServiceAccess) {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeMetadataInitialisationBatch = new
                BatchOfRetryableTasks(UPDATING_METADATA);

        for (StorageNode storageNode : nodesToInitialize) {
            InitNodeMetadata taskCommand = ecsInternalCommandFactory
                    .createInitNodeMetadataCommandWith(storageNode, distributedServiceAccess.getRingMetadata());

            nodeMetadataInitialisationBatch.addTask(
                    new SimpleRetryableTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES, taskCommand));
        }
        return nodeMetadataInitialisationBatch;
    }

    public AbstractBatchOfTasks<AbstractRetryableTask> createNodeMetadataUpdateBatchWith
            (List<StorageNode> nodesToUpdate, RingMetadata ringMetadata, EcsStatus ecsStatus) {
        AbstractBatchOfTasks<AbstractRetryableTask> nodeMetadataUpdateBatch = new
                BatchOfRetryableTasks(UPDATING_METADATA);

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
