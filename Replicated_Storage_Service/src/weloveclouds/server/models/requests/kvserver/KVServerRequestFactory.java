package weloveclouds.server.models.requests.kvserver;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.core.requests.ICallbackRegister;
import weloveclouds.server.core.requests.IRequestFactory;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVServerRequestFactory
        implements IRequestFactory<KVTransferMessage, IKVServerRequest> {

    private static final Logger LOGGER = Logger.getLogger(KVServerRequestFactory.class);

    private IMovableDataAccessService dataAccessService;

    public KVServerRequestFactory(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public IKVServerRequest createRequestFromReceivedMessage(KVTransferMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVServerRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case TRANSFER_ENTRIES:
                request = new Transfer(dataAccessService, receivedMessage.getStorageUnits());
                break;
            case REMOVE_ENTRY_BY_KEY:
                request = new RemoveEntry(dataAccessService, receivedMessage.getRemovableKey());
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

