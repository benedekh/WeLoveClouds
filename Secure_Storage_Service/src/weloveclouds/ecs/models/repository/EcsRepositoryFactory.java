package weloveclouds.ecs.models.repository;

import java.io.File;
import java.util.List;

import com.google.inject.Inject;

import weloveclouds.ecs.exceptions.configuration.InvalidConfigurationException;
import weloveclouds.ecs.utils.IParser;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfiguration;

/**
 * Created by Benoit on 2016-11-21.
 */
public class EcsRepositoryFactory {
    private IParser<List<StorageNode>, File> configurationFileParser;

    @Inject
    public EcsRepositoryFactory(IParser<List<StorageNode>, File> configurationFileParser) {
        this.configurationFileParser = configurationFileParser;
    }

    public EcsRepository createEcsRepositoryFrom(File storageNodesConfigurationFile,
                                                 LoadBalancerConfiguration loadBalancerConfiguration)
            throws InvalidConfigurationException {
        try {
            EcsRepository ecsRepository;
            List<StorageNode> storageNodes = configurationFileParser.parse(storageNodesConfigurationFile);
            LoadBalancer loadBalancer = new LoadBalancer.Builder()
                    .name("LoadBalancer")
                    .host(loadBalancerConfiguration.getHost())
                    .clientRequestInterceptorPort(loadBalancerConfiguration.getClientRequestInterceptorPort())
                    .ecsNotificationServicePort(loadBalancerConfiguration.getEcsNotificationServicePort())
                    .healthMonitoringServicePort(loadBalancerConfiguration.getHealthMonitoringServicePort())
                    .build();

            if (storageNodes == null || storageNodes.isEmpty()) {
                throw new InvalidConfigurationException("Unable to initialize ecs repository with the" +
                        " given configuration file. Please provide a valid configuration file");
            } else {
                ecsRepository = new EcsRepository(storageNodes, loadBalancer);
            }
            return ecsRepository;
        } catch (Exception e) {
            throw new InvalidConfigurationException("Unable to initialize ecs repository with " +
                    "cause: " + e.getMessage(), e);
        }
    }
}
