package weloveclouds.ecs.models.commands.internal;

import com.google.inject.Inject;

import java.util.Arrays;

import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.configuration.providers.NotificationServiceConfigurationProvider;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.ecs.models.commands.internal.ssh.LaunchJar;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.INotificationRequest;
import weloveclouds.ecs.models.repository.LoadBalancer;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.ssh.SecureShellServiceFactory;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;

/**
 * Created by Benoit on 2016-11-23.
 */
public class EcsInternalCommandFactory {
    private static final String LOG_LEVEL_ALL = "ALL";
    private CommunicationApiFactory communicationApiFactory;
    private SecureShellServiceFactory secureShellServiceFactory;

    @Inject
    public EcsInternalCommandFactory(CommunicationApiFactory communicationApiFactory,
                                     SecureShellServiceFactory secureShellServiceFactory) {
        this.communicationApiFactory = communicationApiFactory;
        this.secureShellServiceFactory = secureShellServiceFactory;
    }

    public LaunchJar createLaunchLoadBalancerJarCommandWith(LoadBalancer loadbalancer,
                                                            String jarFilePath) {
        return new LaunchJar.Builder()
                .jarFilePath(jarFilePath)
                .arguments(Arrays.asList(jarFilePath,
                        Integer.toString(loadbalancer.getPort()),
                        Integer.toString(loadbalancer.getHealthMonitoringServiceEndpoint().getPort()),
                        Integer.toString(loadbalancer.getEcsChannelConnectionInfo().getPort()),
                        Integer.toString(NotificationServiceConfigurationProvider
                                .getNotificationServicePort())))
                .secureShellService(secureShellServiceFactory.createJshSecureShellService())
                .targetedNode(loadbalancer)
                .build();
    }

    public LaunchJar createLaunchStorageNodesJarsCommandWith(LoadBalancer loadbalancer,
                                                             StorageNode storageNode,
                                                             String jarFilePath, int cacheSize,
                                                             String displacementStrategy) {
        return new LaunchJar.Builder()
                .jarFilePath(jarFilePath)
                .arguments(Arrays.asList(jarFilePath, Integer.toString(storageNode.getPort()),
                        Integer.toString(storageNode.getKvChannelConnectionInfo().getPort()),
                        Integer.toString(storageNode.getEcsChannelConnectionInfo().getPort()),
                        Integer.toString(cacheSize),
                        displacementStrategy,
                        LOG_LEVEL_ALL,
                        storageNode.getName(),
                        loadbalancer.getIpAddress().replaceAll("/", ""),
                        Integer.toString(loadbalancer.getHealthMonitoringServiceEndpoint()
                                .getPort())))
                .secureShellService(secureShellServiceFactory.createJshSecureShellService())
                .targetedNode(storageNode)
                .build();
    }

    public InitNodeMetadata createInitNodeMetadataCommandWith(StorageNode storageNode,
                                                              RingMetadata ringMetadata) {
        return new InitNodeMetadata.Builder()
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .ringMetadata(ringMetadata)
                .targetedNode(storageNode)
                .build();
    }

    public SetWriteLock createSetWriteLockCommandFor(StorageNode storageNode) {
        return new SetWriteLock.Builder()
                .targetedNode(storageNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .build();
    }

    public ShutdownNode createShutDownNodeCommandFor(StorageNode storageNode) {
        return new ShutdownNode.Builder()
                .targetedNode(storageNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .build();
    }

    public StartNode createStartNodeCommandFor(StorageNode storageNode) {
        return new StartNode.Builder()
                .targetedNode(storageNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .build();
    }

    public StopNode createStopNodeCommandFor(StorageNode storageNode) {
        return new StopNode.Builder()
                .targetedNode(storageNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .build();
    }

    public UpdateMetadata createUpdateMetadataCommandWith(StorageNode storageNode, RingMetadata
            ringMetadata) {
        return new UpdateMetadata.Builder()
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .ringMetadata(ringMetadata)
                .targetedNode(storageNode)
                .build();
    }

    public ReleaseWriteLock createReleaseWriteLockCommandFor(StorageNode storageNode) {
        return new ReleaseWriteLock.Builder()
                .targetedNode(storageNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .build();
    }

    public InvokeDataTransfer createInvokeDataTransferCommandWith(StorageNode targetedNode,
                                                                  StorageNode newNode,
                                                                  RingMetadata ringMetadata,
                                                                  boolean isNodeRemoval) {
        return new InvokeDataTransfer.Builder()
                .targetedNode(targetedNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .newNode(newNode)
                .ringMetadata(ringMetadata)
                .isNodeRemoval(isNodeRemoval)
                .build();
    }

    public NotifyTargetCommand createNotifyTargetCommandWith(
            INotificationRequest<IKVEcsNotificationMessage> notificationRequest,
            IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> messageSerializer) {
        return new NotifyTargetCommand.Builder()
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .notificationRequest(notificationRequest)
                .messageSerializer(messageSerializer)
                .build();
    }
}
