package weloveclouds.ecs.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.exceptions.InvalidConfigurationException;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
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
import weloveclouds.ecs.models.tasks.IBatchTasks;
import weloveclouds.ecs.models.tasks.SimpleRetryableTask;
import weloveclouds.ecs.services.ISecureShellService;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.ListUtils;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.ecs.core.ExternalConfigurationServiceConstants.*;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.IDLE;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.RUNNING;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService {
    private static final String JAR_FILE_PATH = "";
    private String configurationFilePath;
    private EcsRepository repository;
    private EcsRepositoryFactory ecsRepositoryFactory;
    private ITaskService taskService;
    private CommunicationApiFactory communicationApiFactory;
    private ISecureShellService secureShellService;
    private IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer;
    private IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer;


    public ExternalConfigurationService(Builder externalConfigurationServiceBuilder) throws ServiceBootstrapException {
        this.taskService = externalConfigurationServiceBuilder.taskService;
        this.communicationApiFactory = externalConfigurationServiceBuilder.communicationApiFactory;
        this.secureShellService = externalConfigurationServiceBuilder.secureShellService;
        this.messageDeserializer = externalConfigurationServiceBuilder.messageDeserializer;
        this.messageSerializer = externalConfigurationServiceBuilder.messageSerializer;
        this.ecsRepositoryFactory = externalConfigurationServiceBuilder.ecsRepositoryFactory;
        this.configurationFilePath = externalConfigurationServiceBuilder.configurationFilePath;
        bootstrap();
    }

    @SuppressWarnings("unchecked")
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) {
        IBatchTasks<AbstractRetryableTask> nodeInitialisationBatch = new BatchRetryableTasks();
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
        }

        taskService.launchBatchTasks(nodeInitialisationBatch);
    }

    @SuppressWarnings("unchecked")
    public void start() {
        IBatchTasks<AbstractRetryableTask> nodeStartBatch = new BatchRetryableTasks();

        for (StorageNode node : repository.getNodesWithStatus(INITIALIZED)) {
            StartNode taskCommand = new StartNode(communicationApiFactory
                    .createConcurrentCommunicationApiV1(), node);

            nodeStartBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_START_RETRIES, taskCommand));
        }

        taskService.launchBatchTasks(nodeStartBatch);
    }

    public void stop() {
        IBatchTasks<AbstractRetryableTask> nodeStopBatch = new BatchRetryableTasks();

        for (StorageNode node : repository.getNodesWithStatus(RUNNING)) {
            StopNode taskCommand = new StopNode(communicationApiFactory
                    .createConcurrentCommunicationApiV1(), node);

            nodeStopBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_STOP_RETRIES, taskCommand));
        }

        taskService.launchBatchTasks(nodeStopBatch);
    }

    public void shutDown() {
        IBatchTasks<AbstractRetryableTask> nodeShutdownBatch = new BatchRetryableTasks();
        List<StorageNodeStatus> activeNodeStatus = Arrays.asList(INITIALIZED, RUNNING);

        for (StorageNode node : repository.getNodeWithStatus(activeNodeStatus)) {
            ShutdownNode taskCommand = new ShutdownNode(communicationApiFactory
                    .createConcurrentCommunicationApiV1(), node);

            nodeShutdownBatch.addTask(new SimpleRetryableTask(MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES, taskCommand));
        }
    }

    public void addNode(int cacheSize, String displacementStrategy) {

    }

    public void removeNode() {

    }

    private void bootstrap() throws ServiceBootstrapException {
        try {
            repository = ecsRepositoryFactory.createEcsRepositoryFrom(new File(configurationFilePath));
        } catch (InvalidConfigurationException ex) {
            throw new ServiceBootstrapException("Bootstrap failed. Unable to start the service : "
                    + ex.getMessage(), ex);
        }
    }

    public static class Builder {
        private String configurationFilePath;
        private EcsRepositoryFactory ecsRepositoryFactory;
        private ITaskService taskService;
        private CommunicationApiFactory communicationApiFactory;
        private ISecureShellService secureShellService;
        private IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer;
        private IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer;

        public Builder CommunicationApiFactory(CommunicationApiFactory communicationApiFactory) {
            this.communicationApiFactory = communicationApiFactory;
            return this;
        }

        public Builder secureShellService(ISecureShellService secureShellService) {
            this.secureShellService = secureShellService;
            return this;
        }

        public Builder messageSerializer(IMessageSerializer<SerializedMessage, KVAdminMessage>
                                                 messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public Builder messageDeserializer(IMessageDeserializer<KVAdminMessage, SerializedMessage>
                                                   messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
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
