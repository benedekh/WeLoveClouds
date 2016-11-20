package weloveclouds.ecs.core;

import java.util.Arrays;
import java.util.List;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.ecs.models.commands.UpdateServerRepository;
import weloveclouds.ecs.models.commands.ssh.LaunchJar;
import weloveclouds.ecs.models.repository.ServerRepository;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.BatchRetryableTasks;
import weloveclouds.ecs.models.tasks.IBatchTasks;
import weloveclouds.ecs.models.tasks.InitialiseNodeTask;
import weloveclouds.ecs.services.ISecureShellService;
import weloveclouds.ecs.services.ITaskService;
import weloveclouds.ecs.utils.ListUtils;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService {
    private static final String JAR_FILE_PATH = "";
    private static final int MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES = 3;
    private ServerRepository repository;
    private ITaskService taskService;
    private IConcurrentCommunicationApi concurrentCommunicationApi;
    private ISecureShellService secureShellService;
    private IMessageSerializer messageSerializer;
    private IMessageDeserializer messageDeserializer;


    public ExternalConfigurationService(ExternalConfigurationServiceBuilder externalConfigurationServiceBuilder) {
    }

    @SuppressWarnings("unchecked")
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) {
        IBatchTasks<AbstractRetryableTask> nodeInitialisationBatch = new BatchRetryableTasks();
        List<StorageNode> storageNodesToInitialize = (List<StorageNode>) ListUtils
                .getPreciseNumberOfRandomObjectsFrom(repository.getIdledNodes(), numberOfNodes);

        for (StorageNode storageNode : storageNodesToInitialize) {
            LaunchJar taskCommand = new LaunchJar.Builder()
                    .jarFilePath(JAR_FILE_PATH)
                    .arguments(Arrays.asList(Integer.toString(cacheSize), displacementStrategy))
                    .secureShellService(secureShellService)
                    .targettedNode(storageNode)
                    .build();

            UpdateServerRepository successTask = new UpdateServerRepository(repository);

            nodeInitialisationBatch.addTask(
                    new InitialiseNodeTask(MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES,
                            taskCommand, successTask));
        }

        taskService.launchBatchTasks(nodeInitialisationBatch);
    }

    public void start() {

    }

    public void stop() {

    }

    public void shutDown() {

    }

    public void addNode(int cacheSize, String displacementStrategy) {

    }

    public void removeNode() {

    }

    public static class ExternalConfigurationServiceBuilder {
        private IConcurrentCommunicationApi concurrentCommunicationApi;
        private ISecureShellService secureShellService;
        private IMessageSerializer messageSerializer;
        private IMessageDeserializer messageDeserializer;

        ExternalConfigurationServiceBuilder concurrentCommunicationApi
                (IConcurrentCommunicationApi concurrentCommunicationApi) {
            this.concurrentCommunicationApi = concurrentCommunicationApi;
            return this;
        }

        ExternalConfigurationServiceBuilder secureShellService(ISecureShellService secureShellService) {
            this.secureShellService = secureShellService;
            return this;
        }

        ExternalConfigurationServiceBuilder messageSerializer(IMessageSerializer messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        ExternalConfigurationServiceBuilder messageDeserializer(IMessageDeserializer messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        ExternalConfigurationService build() {
            return new ExternalConfigurationService(this);
        }
    }
}
