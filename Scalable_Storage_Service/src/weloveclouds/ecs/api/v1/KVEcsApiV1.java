package weloveclouds.ecs.api.v1;

import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.services.ICommunicationService;
import weloveclouds.ecs.api.IKVEcsApi;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVEcsApiV1 extends CommunicationApiV1 implements IKVEcsApi{
    public KVEcsApiV1(ICommunicationService communicationService) {
        super(communicationService);
    }

    @Override
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void shutDown() {

    }

    @Override
    public void addNode(int cacheSize, String displacementStrategy) {

    }

    @Override
    public void removeNode() {

    }
}
