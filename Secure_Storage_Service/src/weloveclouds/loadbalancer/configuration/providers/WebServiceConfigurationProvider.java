package weloveclouds.loadbalancer.configuration.providers;

/**
 * Created by Benoit on 2017-01-22.
 */
public class WebServiceConfigurationProvider {
    private static final int PORT = 8080;

    public static int getPort() {
        return PORT;
    }
}
