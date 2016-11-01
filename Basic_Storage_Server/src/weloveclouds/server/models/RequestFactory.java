package weloveclouds.server.models;

import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.api.IKVServerApi;
import weloveclouds.server.models.requests.Get;
import weloveclouds.server.models.requests.IRequest;
import weloveclouds.server.services.IDataAccessService;

/**
 * Created by Benoit on 2016-10-31.
 */
public class RequestFactory {
    private IDataAccessService dataAccessService;

    public RequestFactory(IDataAccessService dataAccessService){
        this.dataAccessService = dataAccessService;
    }

    synchronized public IRequest createRequestFromReceivedMessage(KVMessage message) {
        IRequest request = null;

        switch (message.getStatus()) {
            case GET:
                request = new Get(dataAccessService, message.getKey());
                break;
            case PUT:
                break;
            case DELETE:
                break;
        }

        return request;
    }
}
