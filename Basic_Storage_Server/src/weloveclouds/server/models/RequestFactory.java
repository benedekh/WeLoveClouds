package weloveclouds.server.models;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.server.models.requests.IRequest;

/**
 * Created by Benoit on 2016-10-31.
 */
public class RequestFactory {
    private IConcurrentCommunicationApi communicationApi;

    public RequestFactory(IConcurrentCommunicationApi communicationApi) {
        this.communicationApi = communicationApi;
    }

    synchronized public IRequest createRequestFromReceivedMessage(){
        return null;
    }
}
