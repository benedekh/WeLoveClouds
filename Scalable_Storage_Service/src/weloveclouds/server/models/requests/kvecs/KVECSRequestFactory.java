package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
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
    private volatile IMovableDataAccessService dataAccessService;

    private ICommunicationApi communicationApi;

    public KVECSRequestFactory(DataAccessServiceFactory dataAccessServiceFactory,
            IMovableDataAccessService dataAccessService,
            CommunicationApiFactory communicationApiFactory) {

        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.dataAccessService = dataAccessService;
        this.communicationApi = communicationApiFactory.createCommunicationApiV1();
    }

    public IKVECSRequest createRequestFromReceivedMessage(KVAdminMessage receivedMessage) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                request = new InitializeKVServer(dataAccessServiceFactory, dataAccessService,
                        receivedMessage.getInitializationContext());
                new UpdateRingMetadata(dataAccessService, receivedMessage.getRingMetadata(),
                        receivedMessage.getTargetServerInfo().getRange()).execute();
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
                request = new MoveDataToDestination(dataAccessService,
                        receivedMessage.getTargetServerInfo(), communicationApi);
                break;
            case UPDATE:
                request =
                        new UpdateRingMetadata(dataAccessService, receivedMessage.getRingMetadata(),
                                receivedMessage.getTargetServerInfo().getRange());
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

