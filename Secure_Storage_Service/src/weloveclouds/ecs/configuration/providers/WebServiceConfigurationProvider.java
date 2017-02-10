package weloveclouds.ecs.configuration.providers;

/**
 * Created by Benoit on 2017-01-22.
 */
public class WebServiceConfigurationProvider {
    private static final int PORT = 8081;
    private static final String JERSEY_RESOURCES_CONFIG_CLASS = "weloveclouds.ecs" +
            ".configuration.JerseyConfig";

    public static int getPort() {
        return PORT;
    }

    public static String getJerseyResourcesConfigClass() {
        return JERSEY_RESOURCES_CONFIG_CLASS;
    }
}
