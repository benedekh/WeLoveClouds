package weloveclouds.ecs.configuration.providers;

import java.io.IOException;


/**
 * Created by Benoit on 2016-11-18.
 */
public class StorageNodeConfigurationProvider {
    private static StorageNodeConfigurationProvider INSTANCE;

    private StorageNodeConfigurationProvider() {

    }

    public static StorageNodeConfigurationProvider getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new StorageNodeConfigurationProvider();
        }
        return INSTANCE;
    }
}
