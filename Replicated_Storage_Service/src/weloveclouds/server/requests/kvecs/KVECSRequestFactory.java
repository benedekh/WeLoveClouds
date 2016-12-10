package weloveclouds.server.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.core.requests.ICallbackRegister;
import weloveclouds.server.core.requests.IRequestFactory;
import weloveclouds.server.requests.kvecs.utils.StorageUnitsTransporterFactory;
import weloveclouds.server.services.IReplicableDataAccessService;
import weloveclouds.server.services.utils.ReplicationTransfererFactory;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVECSRequestFactory implements IRequestFactory<KVAdminMessage, IKVECSRequest> {

    private static final Logger LOGGER = Logger.getLogger(KVECSRequestFactory.class);

    private IReplicableDataAccessService dataAccessService;
    private ICommunicationApi communicationApi;

    private StorageUnitsTransporterFactory storageUnitsTransporterFactory;
    private ReplicationTransfererFactory replicationTransfererFactory;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

    protected KVECSRequestFactory(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.communicationApi = builder.communicationApi;
        this.storageUnitsTransporterFactory = builder.storageUnitsTransporterFactory;
        this.replicationTransfererFactory = builder.replicationTransfererFactory;
        this.transferMessageSerializer = builder.transferMessageSerializer;
        this.transferMessageDeserializer = builder.transferMessageDeserializer;
    }

    @Override
    public IKVECSRequest createRequestFromReceivedMessage(KVAdminMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                request = new InitializeKVServer.Builder().dataAccessService(dataAccessService)
                        .replicationTransfererFactory(replicationTransfererFactory)
                        .ringMetadata(receivedMessage.getRingMetadata())
                        .readRanges(receivedMessage.getTargetServerInfo().getReadRanges())
                        .writeRange(receivedMessage.getTargetServerInfo().getWriteRange())
                        .replicaConnectionInfos(receivedMessage.getReplicaConnectionInfos())
                        .build();
                break;
            case START:
                request = new StartDataAcessService(dataAccessService);
                break;
            case STOP:
                request = new StopDataAccessService(dataAccessService);
                break;
            case LOCKWRITE:
                request = new LockWriteAccess(dataAccessService);
                break;
            case UNLOCKWRITE:
                request = new UnlockWriteAccess(dataAccessService);
                break;
            case COPYDATA:
                request = new CopyDataToDestination.Builder().dataAccessService(dataAccessService)
                        .communicationApi(communicationApi)
                        .targetServerInfo(receivedMessage.getTargetServerInfo())
                        .transferMessageSerializer(transferMessageSerializer)
                        .transferMessageDeserializer(transferMessageDeserializer)
                        .storageUnitsTransporterFactory(storageUnitsTransporterFactory).build();
                break;
            case MOVEDATA:
                request = new MoveDataToDestination.Builder().dataAccessService(dataAccessService)
                        .communicationApi(communicationApi)
                        .targetServerInfo(receivedMessage.getTargetServerInfo())
                        .transferMessageSerializer(transferMessageSerializer)
                        .transferMessageDeserializer(transferMessageDeserializer)
                        .storageUnitsTransporterFactory(storageUnitsTransporterFactory).build();
                break;
            case REMOVERANGE:
                request = new RemoveRange(dataAccessService, receivedMessage.getRemovableRange());
                break;
            case UPDATE:
                request = new UpdateRingMetadata.Builder().dataAccessService(dataAccessService)
                        .replicationTransfererFactory(replicationTransfererFactory)
                        .ringMetadata(receivedMessage.getRingMetadata())
                        .readRanges(receivedMessage.getTargetServerInfo().getReadRanges())
                        .writeRange(receivedMessage.getTargetServerInfo().getWriteRange())
                        .replicaConnectionInfos(receivedMessage.getReplicaConnectionInfos())
                        .build();
                break;
            case SHUTDOWN:
                request = new ShutdownServer(callbackRegister);
                break;
            default:
                String errorMessage = "Unrecognized command for KVAdmin message";
                LOGGER.error(join(" ", errorMessage, receivedMessage.toString()));
                request = new DefaultRequest(errorMessage);
                break;
        }

        return request;
    }

    /**
     * Builder pattern for creating a {@link KVECSRequestFactory} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IReplicableDataAccessService dataAccessService;
        private ICommunicationApi communicationApi;
        private StorageUnitsTransporterFactory storageUnitsTransporterFactory;
        private ReplicationTransfererFactory replicationTransfererFactory;
        private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
        private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public Builder communicationApiFactory(CommunicationApiFactory communicationApiFactory) {
            this.communicationApi = communicationApiFactory.createCommunicationApiV1();
            return this;
        }

        public Builder storageUnitsTransporterFactory(
                StorageUnitsTransporterFactory storageUnitsTransporterFactory) {
            this.storageUnitsTransporterFactory = storageUnitsTransporterFactory;
            return this;
        }

        public Builder replicationTransfererFactory(
                ReplicationTransfererFactory replicationTransfererFactory) {
            this.replicationTransfererFactory = replicationTransfererFactory;
            return this;
        }

        public Builder transferMessageSerializer(
                IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer) {
            this.transferMessageSerializer = transferMessageSerializer;
            return this;
        }

        public Builder transferMessageDeserializer(
                IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
            this.transferMessageDeserializer = transferMessageDeserializer;
            return this;
        }

        public KVECSRequestFactory build() {
            return new KVECSRequestFactory(this);
        }
    }
}

