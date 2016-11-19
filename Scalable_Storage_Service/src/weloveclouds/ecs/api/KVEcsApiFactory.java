package weloveclouds.ecs.api;

import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.ecs.api.v1.KVEcsApiV1;

/**
 * Created by Benoit on 2016-11-18.
 */
public class KVEcsApiFactory {
    public IKVEcsApi createKVEscApiV1(){
        return new KVEcsApiV1(new ConcurrentCommunicationService());
    }
}
