package weloveclouds.ecs.api;

import weloveclouds.ecs.core.EcsStatus;
import weloveclouds.ecs.exceptions.ExternalConfigurationServiceException;
import weloveclouds.ecs.models.repository.EcsRepository;
import weloveclouds.ecs.models.stats.EcsStatistics;

/**
 * Created by Benoit on 2016-11-15.
 */
public interface IKVEcsApi {
    EcsStatus getStatus();

    EcsRepository getRepository();

    void initService(int numberOfNodes, int cacheSize, String displacementStrategy) throws ExternalConfigurationServiceException;

    void startLoadBalancer() throws ExternalConfigurationServiceException;

    void start() throws ExternalConfigurationServiceException;

    void stop() throws ExternalConfigurationServiceException;

    void shutDown() throws ExternalConfigurationServiceException;

    void addNode(int cacheSize, String displacementStrategy, boolean withAutomaticStart) throws
            ExternalConfigurationServiceException;

    void removeNode() throws ExternalConfigurationServiceException;

    EcsStatistics getStats() throws ExternalConfigurationServiceException;

    void removeUnresponsiveNodesWithName(String name) throws ExternalConfigurationServiceException;
}
