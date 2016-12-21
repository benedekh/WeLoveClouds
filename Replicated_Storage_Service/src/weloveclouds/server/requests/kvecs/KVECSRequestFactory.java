package weloveclouds.server.requests.kvecs;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvecs.utils.StorageUnitsTransporterFactory;
import weloveclouds.server.services.datastore.IReplicableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVECSRequestFactory implements IRequestFactory<IKVAdminMessage, IKVECSRequest> {

    private static final Logger LOGGER = Logger.getLogger(KVECSRequestFactory.class);

    private IReplicableDataAccessService dataAccessService;
    private StorageUnitsTransporterFactory storageUnitsTransporterFactory;

    protected KVECSRequestFactory(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.storageUnitsTransporterFactory = builder.storageUnitsTransporterFactory;
    }

    @Override
    public IKVECSRequest createRequestFromReceivedMessage(IKVAdminMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                request = new InitializeKVServer.Builder().dataAccessService(dataAccessService)
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
                        .targetServerInfo(receivedMessage.getTargetServerInfo())
                        .storageUnitsTransporterFactory(storageUnitsTransporterFactory).build();
                break;
            case MOVEDATA:
                request = new MoveDataToDestination.Builder().dataAccessService(dataAccessService)
                        .targetServerInfo(receivedMessage.getTargetServerInfo())
                        .storageUnitsTransporterFactory(storageUnitsTransporterFactory).build();
                break;
            case REMOVERANGE:
                request = new RemoveRange(dataAccessService, receivedMessage.getRemovableRange());
                break;
            case UPDATE:
                request = new UpdateRingMetadata.Builder().dataAccessService(dataAccessService)
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
                LOGGER.error(StringUtils.join(" ", errorMessage, receivedMessage));
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
        private StorageUnitsTransporterFactory storageUnitsTransporterFactory;

        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public Builder storageUnitsTransporterFactory(
                StorageUnitsTransporterFactory storageUnitsTransporterFactory) {
            this.storageUnitsTransporterFactory = storageUnitsTransporterFactory;
            return this;
        }

        public KVECSRequestFactory build() {
            return new KVECSRequestFactory(this);
        }
    }
}

