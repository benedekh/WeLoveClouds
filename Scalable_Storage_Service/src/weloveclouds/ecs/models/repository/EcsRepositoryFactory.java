package weloveclouds.ecs.models.repository;

import java.io.File;
import java.util.List;

import weloveclouds.ecs.exceptions.InvalidConfigurationException;
import weloveclouds.ecs.utils.ConfigurationFileParser;

/**
 * Created by Benoit on 2016-11-21.
 */
public class EcsRepositoryFactory {
    private ConfigurationFileParser configurationFileParser;

    public EcsRepositoryFactory(ConfigurationFileParser configurationFileParser) {
        this.configurationFileParser = configurationFileParser;
    }

    public EcsRepository createEcsRepositoryFrom(File configurationFile) throws InvalidConfigurationException {
        EcsRepository ecsRepository = null;
        List<StorageNode> storageNodes = configurationFileParser.parse(configurationFile);

        if (storageNodes == null || storageNodes.isEmpty()) {
            throw new InvalidConfigurationException("Unable to initialize ecs repository with the" +
                    " given configuration file. Please provide a valid configuration file");
        } else {
            ecsRepository = new EcsRepository(storageNodes);
        }
        return ecsRepository;
    }
}
