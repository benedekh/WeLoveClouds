package weloveclouds.communication;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.communication.services.ConcurrentCommunicationService;

/**
 * Created by Benoit on 2016-11-01.
 */
public class CommunicationApiFactory {

    public ICommunicationApi createCommunicationApiV1() {
        return new CommunicationApiV1(new CommunicationService(new SocketFactory()));
    }

    public IConcurrentCommunicationApi createConcurrentCommunicationApiV1(){
        return new ConcurrentCommunicationApiV1(new ConcurrentCommunicationService());
    }
}
