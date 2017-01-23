package weloveclouds.loadbalancer.configuration.providers;

/**
 * Created by Benoit on 2017-01-22.
 */
public class WebServiceConfigurationProvider {
    private static final int PORT = 8080;
    private static final String JERSEY_RESOURCES_CONFIF_CLASS = "weloveclouds.loadbalancer" +
            ".configuration.JerseyConfig";

    public static int getPort() {
        return PORT;
    }

    public static String getJerseyResourcesConfifClass() {
        return JERSEY_RESOURCES_CONFIF_CLASS;
    }
}
