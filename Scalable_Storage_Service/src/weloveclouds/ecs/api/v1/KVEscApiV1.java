package weloveclouds.ecs.api.v1;

import weloveclouds.ecs.api.IKVEscApi;
import weloveclouds.ecs.core.ExternalConfigurationService;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVEscApiV1 implements IKVEscApi {
    private ExternalConfigurationService externalConfigurationService;

    public KVEscApiV1(ExternalConfigurationService externalConfigurationService) {
        this.externalConfigurationService = externalConfigurationService;
    }

    @Override
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) {
        externalConfigurationService.initService(numberOfNodes, cacheSize, displacementStrategy);
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
