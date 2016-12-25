package weloveclouds.commons.monitoring.statsd.configuration;

/**
 * Created by Benoit on 2016-11-27.
 */
public class StatsdConfigurationProvider {
    private static final String STATSD_SERVER_ADDRESS = "192.168.229.129";
    private static final int DEFAULT_STATSD_SERVICE_PORT = 8125;
    private static final StatsdConfigurationProvider instance = new StatsdConfigurationProvider();

    private StatsdConfigurationProvider() {}

    public int getStatsdServicePort() {
        return DEFAULT_STATSD_SERVICE_PORT;
    }

    public String getStatsdServerAddress() {
        return STATSD_SERVER_ADDRESS;
    }

    public static StatsdConfigurationProvider getInstance() {
        return instance;
    }
}
