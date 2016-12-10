package weloveclouds.ecs.contexts;


/**
 * Created by Benoit on 2016-12-03.
 */
public class EcsExecutionContext {
    private static final String ECS_CONFIGURATION_FILE_PROPERTIES = "ecsConfigurationFilePath";

    public static void setConfigurationFilePath(String ecsConfigurationFilePath) {
        System.setProperty(ECS_CONFIGURATION_FILE_PROPERTIES, ecsConfigurationFilePath);
    }

    public static String getConfigurationFilePath() {
        return System.getProperty(ECS_CONFIGURATION_FILE_PROPERTIES);
    }
}
