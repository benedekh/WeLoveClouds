package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.DataAccessServiceFactory;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVECSRequestFactory {

    private static final Logger LOGGER = Logger.getLogger(KVECSRequestFactory.class);

    private DataAccessServiceFactory dataAccessServiceFactory;
    private IMovableDataAccessService dataAccessService;

    private RingMetadata ringMetadata;
    private RingMetadataPart rangeManagedByServer;

    public KVECSRequestFactory(DataAccessServiceFactory dataAccessServiceFactory,
            IMovableDataAccessService dataAccessService, RingMetadata ringMetadata,
            RingMetadataPart rangeManagedByServer) {

        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.dataAccessService = dataAccessService;
        this.ringMetadata = ringMetadata;
        this.rangeManagedByServer = rangeManagedByServer;
    }

    public IKVECSRequest createRequestFromReceivedMessage(KVAdminMessage receivedMessage) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                request = new InitializeKVServer(dataAccessServiceFactory, dataAccessService,
                        receivedMessage.getInitializationContext());
                new UpdateRingMetadata(ringMetadata, rangeManagedByServer,
                        receivedMessage.getRingMetadata(), receivedMessage.getServerInfo())
                                .execute();
                break;
            case START:
                new StartDataAcessService(dataAccessService);
                break;
            case STOP:
                new StopDataAccessService(dataAccessService);
                break;
            case LOCKWRITE:
                new LockWriteAccess(dataAccessService);
                break;
            case UNLOCKWRITE:
                new UnlockWriteAccess(dataAccessService);
                break;
            case MOVEDATA:
                new MoveDataToDestination(dataAccessService, receivedMessage.getServerInfo());
                break;
            case UPDATE:
                new UpdateRingMetadata(ringMetadata, rangeManagedByServer,
                        receivedMessage.getRingMetadata(), receivedMessage.getServerInfo());
                break;
            case SHUTDOWN:
                new ShutdownServer();
                break;
            default:
                String errorMessage = "Unrecognized command for transfer message";
                LOGGER.error(join(" ", errorMessage, receivedMessage.toString()));
                request = new DefaultRequest(errorMessage);
                break;
        }

        return request;
    }
}

