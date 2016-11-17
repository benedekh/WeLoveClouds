package weloveclouds.ecs.api;

import weloveclouds.communication.api.ICommunicationApi;

/**
 * Created by Benoit on 2016-11-15.
 */
public interface IKVEscApi{
    void initService(int numberOfNodes, int cacheSize, String displacementStrategy);
    void start();
    void stop();
    void shutDown();
    void addNode(int cacheSize, String displacementStrategy);
    void removeNode();
}
