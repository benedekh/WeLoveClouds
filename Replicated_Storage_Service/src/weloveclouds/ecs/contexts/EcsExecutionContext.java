package weloveclouds.ecs.contexts;

import weloveclouds.commons.context.Environment;

/**
 * Created by Benoit on 2016-12-03.
 */
public class EcsExecutionContext {
    public static final String ECS_CONFIGURATION_FILE_PROPERTIES = "environment";


    public static void setConfigurationFilePath(String ecsConfigurationFilePath) {
        System.setProperty(ECS_CONFIGURATION_FILE_PROPERTIES,ecsConfigurationFilePath);
    }

    public static String getConfigurationFilePath() {
        return System.getProperty(ECS_CONFIGURATION_FILE_PROPERTIES);
    }
}
