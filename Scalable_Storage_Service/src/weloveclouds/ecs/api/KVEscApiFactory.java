package weloveclouds.ecs.api;

import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.ecs.api.v1.KVEscApiV1;

/**
 * Created by Benoit on 2016-11-18.
 */
public class KVEscApiFactory {
    public IKVEscApi createKVEscApiV1(){
        return new KVEscApiV1(new ConcurrentCommunicationService());
    }
}
