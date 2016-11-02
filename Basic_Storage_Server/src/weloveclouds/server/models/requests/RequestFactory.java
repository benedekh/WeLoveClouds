package weloveclouds.server.models.requests;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;

/**
 * Created by Benoit on 2016-10-31.
 */
public class RequestFactory {
    private IDataAccessService dataAccessService;

    public RequestFactory(IDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    synchronized public IRequest createRequestFromReceivedMessage(KVMessage receivedMessage) {
        IRequest request = null;
        StatusType status = receivedMessage.getStatus();

        // see M2 docs, we delete the key if it is a PUT request with only a key
        if (receivedMessage.getStatus() == StatusType.PUT && receivedMessage.getValue() == null) {
            status = StatusType.DELETE;
        }

        switch (status) {
            case GET:
                request = new Get(dataAccessService, receivedMessage.getKey());
                break;
            case PUT:
                request = new Put(dataAccessService, receivedMessage.getKey(),
                        receivedMessage.getValue());
                break;
            case DELETE:
                request = new Delete(dataAccessService, receivedMessage.getKey());
                break;
            default: // TODO log unknown command
                break;
        }

        return request;
    }
}
