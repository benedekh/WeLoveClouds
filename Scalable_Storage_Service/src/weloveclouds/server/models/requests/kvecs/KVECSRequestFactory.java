package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
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

    private IMovableDataAccessService dataAccessService;

    public KVECSRequestFactory(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    public IKVECSRequest createRequestFromReceivedMessage(KVAdminMessage receivedMessage) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                new InitializeKVServer();
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
                new MoveDataToDestination();
                break;
            case UPDATE:
                new UpdateRingMetadata();
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

