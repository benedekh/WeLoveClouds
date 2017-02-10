package weloveclouds.ecs.api.v1;

import com.google.inject.Inject;


import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.core.EcsStatus;
import weloveclouds.ecs.core.ExternalConfigurationService;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;
import weloveclouds.ecs.models.repository.EcsRepository;
import weloveclouds.ecs.models.stats.EcsStatistics;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVEcsApiV1 implements IKVEcsApi {
    private ExternalConfigurationService externalConfigurationService;

    @Inject
    public KVEcsApiV1(ExternalConfigurationService externalConfigurationService) {
        this.externalConfigurationService = externalConfigurationService;
    }

    @Override
    public EcsStatus getStatus() {
        return externalConfigurationService.getStatus();
    }

    @Override
    public EcsRepository getRepository() {
        return externalConfigurationService.getRepository();
    }

    @Override
    public void initService(int numberOfNodes, int cacheSize, String displacementStrategy) throws ExternalConfigurationServiceException {
        externalConfigurationService.initService(numberOfNodes, cacheSize, displacementStrategy);
    }

    @Override
    public void startLoadBalancer() throws ExternalConfigurationServiceException {
        externalConfigurationService.startLoadBalancer();
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
    public EcsStatistics getStats() throws ExternalConfigurationServiceException {
        return externalConfigurationService.getStats();
    }

    @Override
    public void removeUnresponsiveNodesWithName(String name) throws ExternalConfigurationServiceException {
        externalConfigurationService.removeUnresponsiveNodesWithName(name);
    }

    @Override
    public void shutDown() throws ExternalConfigurationServiceException {
        externalConfigurationService.shutDown();
    }

    @Override
    public void addNode(int cacheSize, String displacementStrategy, boolean withAutomaticStart)
            throws ExternalConfigurationServiceException {
        externalConfigurationService.addNode(cacheSize, displacementStrategy, withAutomaticStart);
    }

    @Override
    public void removeNode() throws ExternalConfigurationServiceException {
        externalConfigurationService.removeNode();
    }
}
