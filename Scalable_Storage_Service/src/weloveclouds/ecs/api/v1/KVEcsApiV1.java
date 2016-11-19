package weloveclouds.ecs.api.v1;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.services.ICommunicationService;
import weloveclouds.communication.services.IConcurrentCommunicationService;
import weloveclouds.ecs.api.IKVEscApi;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVEscApiV1  implements IKVEscApi{
    public KVEscApiV1(IConcurrentCommunicationService communicationService) {

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
