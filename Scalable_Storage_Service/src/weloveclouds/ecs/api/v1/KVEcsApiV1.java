package weloveclouds.ecs.api.v1;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.core.ExternalConfigurationService;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVEcsApiV1 implements IKVEcsApi {
    private ExternalConfigurationService externalConfigurationService;

    public KVEcsApiV1(ExternalConfigurationService externalConfigurationService) {
        this.externalConfigurationService = externalConfigurationService;
    }

    @Override
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) throws ExternalConfigurationServiceException {
        externalConfigurationService.initService(numberOfNodes, cacheSize, displacementStrategy);
    }

    @Override
    public void start() throws ExternalConfigurationServiceException {
        externalConfigurationService.start();
    }

    @Override
    public void stop() throws ExternalConfigurationServiceException {
        externalConfigurationService.stop();
    }

    @Override
    public void shutDown() throws ExternalConfigurationServiceException {
        externalConfigurationService.shutDown();
    }

    @Override
    public void addNode(int cacheSize, String displacementStrategy) throws ExternalConfigurationServiceException {
        externalConfigurationService.addNode(cacheSize, displacementStrategy);
    }

    @Override
    public void removeNode() throws ExternalConfigurationServiceException {
        externalConfigurationService.removeNode();
    }
}
