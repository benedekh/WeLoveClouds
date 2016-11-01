package weloveclouds.server.models;

import weloveclouds.kvstore.KVMessage;
import weloveclouds.server.models.requests.Delete;
import weloveclouds.server.models.requests.Get;
import weloveclouds.server.models.requests.IRequest;
import weloveclouds.server.models.requests.Put;
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

        switch (receivedMessage.getStatus()) {
            case GET:
                request = new Get(dataAccessService, receivedMessage.getKey());
                break;
            case PUT:
                request = new Put(dataAccessService, receivedMessage.getKey(), receivedMessage.getValue());
                break;
            case DELETE:
                request = new Delete(dataAccessService, receivedMessage.getKey());
                break;
        }

        return request;
    }
}
