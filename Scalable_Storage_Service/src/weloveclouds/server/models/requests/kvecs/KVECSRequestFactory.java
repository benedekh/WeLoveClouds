package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVECSRequestFactory implements IRequestFactory<KVAdminMessage, IKVECSRequest> {

    private static final Logger LOGGER = Logger.getLogger(KVECSRequestFactory.class);

    private IMovableDataAccessService dataAccessService;
    private ICommunicationApi communicationApi;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

    public KVECSRequestFactory(IMovableDataAccessService dataAccessService,
            CommunicationApiFactory communicationApiFactory,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
        this.dataAccessService = dataAccessService;
        this.communicationApi = communicationApiFactory.createCommunicationApiV1();
        this.transferMessageSerializer = transferMessageSerializer;
        this.transferMessageDeserializer = transferMessageDeserializer;
    }

    @Override
    public IKVECSRequest createRequestFromReceivedMessage(KVAdminMessage receivedMessage) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                request =
                        new InitializeKVServer(dataAccessService, receivedMessage.getRingMetadata(),
                                receivedMessage.getTargetServerInfo().getRange());
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
                        receivedMessage.getTargetServerInfo(), communicationApi,
                        transferMessageSerializer, transferMessageDeserializer);
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
                String errorMessage = "Unrecognized command for KVAdmin message";
                LOGGER.error(join(" ", errorMessage, receivedMessage.toString()));
                request = new DefaultRequest(errorMessage);
                break;
        }

        return request;
    }
}

