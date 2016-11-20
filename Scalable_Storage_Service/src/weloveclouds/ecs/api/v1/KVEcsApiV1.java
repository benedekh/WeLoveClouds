package weloveclouds.ecs.api.v1;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.core.ExternalConfigurationService;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVEcsApiV1 implements IKVEcsApi {
    private ExternalConfigurationService externalConfigurationService;

    public KVEcsApiV1(ExternalConfigurationService externalConfigurationService) {
        this.externalConfigurationService = externalConfigurationService;
    }

    @Override
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) {
        externalConfigurationService.initService(numberOfNodes, cacheSize, displacementStrategy);
    }

    @Override
    public void start() {
        externalConfigurationService.start();
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
