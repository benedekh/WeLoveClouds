package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
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

    private ICommunicationApi communicationApi;

    private RingMetadata ringMetadata;
    private RingMetadataPart rangeManagedByServer;

    public KVECSRequestFactory(DataAccessServiceFactory dataAccessServiceFactory,
            IMovableDataAccessService dataAccessService,
            CommunicationApiFactory communicationApiFactory, RingMetadata ringMetadata,
            RingMetadataPart rangeManagedByServer) {

        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.dataAccessService = dataAccessService;

        this.communicationApi = communicationApiFactory.createCommunicationApiV1();
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
                        receivedMessage.getRingMetadata(), receivedMessage.getTargetServerInfo())
                                .execute();
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
            case MOVEDATA:
                new LockWriteAccess(dataAccessService).execute();
                request = new MoveDataToDestination(dataAccessService,
                        receivedMessage.getTargetServerInfo(), communicationApi);
                new UnlockWriteAccess(dataAccessService).execute();
                break;
            case UPDATE:
                request = new UpdateRingMetadata(ringMetadata, rangeManagedByServer,
                        receivedMessage.getRingMetadata(), receivedMessage.getTargetServerInfo());
                break;
            case SHUTDOWN:
                request = new ShutdownServer();
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

