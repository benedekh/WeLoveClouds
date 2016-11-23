package weloveclouds.ecs.models.commands.internal;


import java.util.Arrays;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.models.commands.internal.ssh.LaunchJar;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.ssh.SecureShellServiceFactory;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;

/**
 * Created by Benoit on 2016-11-23.
 */
public class EcsInternalCommandFactory {
    private static final String LOG_LEVEL_ALL = "ALL";
    private CommunicationApiFactory communicationApiFactory;
    private SecureShellServiceFactory secureShellServiceFactory;

    public EcsInternalCommandFactory(CommunicationApiFactory communicationApiFactory,
                                     SecureShellServiceFactory secureShellServiceFactory) {
        this.communicationApiFactory = communicationApiFactory;
        this.secureShellServiceFactory = secureShellServiceFactory;
    }

    public LaunchJar createLaunchJarCommandWith(StorageNode storageNode, String jarFilePath, int
            cacheSize, String displacementStrategy) {
        return new LaunchJar.Builder()
                .jarFilePath(jarFilePath)
                .arguments(Arrays.asList(Integer.toString(storageNode.getPort()), Integer.toString(cacheSize),
                        displacementStrategy, LOG_LEVEL_ALL))
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
                                                                  RingMetadata ringMetadata) {
        return new InvokeDataTransfer.Builder()
                .targetedNode(targetedNode)
                .communicationApi(communicationApiFactory.createCommunicationApiV1())
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer())
                .newNode(newNode)
                .ringMetadata(ringMetadata)
                .build();
    }
}
