package weloveclouds.ecs.api;

/**
 * Created by Benoit on 2016-11-15.
 */
public interface IKVEcsApi {
    void initService(int numberOfNodes, int cacheSize, String displacementStrategy);
    void start();
    void stop();
    void shutDown();
    void addNode(int cacheSize, String displacementStrategy);
    void removeNode();
}
