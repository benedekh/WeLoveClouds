package weloveclouds.ecs.configuration.providers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import weloveclouds.ecs.exceptions.configuration.InvalidLoadBalancerConfigurationException;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfiguration;

/**
 * Created by Benoit on 2017-01-18.
 */
public class LoadBalancerConfigurationProvider {
    private static final String LOADBALANCER_PROPERTIES_FILE_PATH = "./loadbalancer.properties";
    private static final String HOST_PROPERTIES = "host";
    private static final String ECS_NOTIFICATION_SERVICE_PORT_PROPERTIES =
            "ecsNotificationServicePort";
    private static final String CLIENT_REQUEST_INTERCEPTOR_PORT_PROPERTIES = "clientRequestInterceptorPort";
    private static final String HEALTH_MONITORING_SERVICE_PORT_PROPERTIES =
            "healthMonitoringServicePort";
    private static LoadBalancerConfigurationProvider INSTANCE = null;

    private Properties properties;
    private LoadBalancerConfiguration loadBalancerConfiguration;

    private LoadBalancerConfigurationProvider() throws IOException, InvalidLoadBalancerConfigurationException {
        loadConfiguration();
    }

    public LoadBalancerConfiguration getLoadBalancerConfiguration() {
        return this.loadBalancerConfiguration;
    }

    private void loadConfiguration() throws IOException, InvalidLoadBalancerConfigurationException {
        try (FileInputStream loadBalancerPropertiesFile = new FileInputStream
                (LOADBALANCER_PROPERTIES_FILE_PATH)) {
            properties = new Properties();
            properties.load(loadBalancerPropertiesFile);
            loadBalancerConfiguration = new LoadBalancerConfiguration.LoadBalancerConfigurationBuilder()
                    .host(properties.getProperty(HOST_PROPERTIES))
                    .ecsNotificationServicePort(Integer.parseInt(properties.getProperty
                            (ECS_NOTIFICATION_SERVICE_PORT_PROPERTIES)))
                    .healthMonitoringServicePort(Integer.parseInt(properties.getProperty
                            (HEALTH_MONITORING_SERVICE_PORT_PROPERTIES)))
                    .clientRequestInterceptorPort(Integer.parseInt(properties.getProperty
                            (CLIENT_REQUEST_INTERCEPTOR_PORT_PROPERTIES)))
                    .build();
        } catch (Exception e) {
            throw new InvalidLoadBalancerConfigurationException();
        }
    }

    public static LoadBalancerConfigurationProvider getInstance() throws IOException, InvalidLoadBalancerConfigurationException {
        if (INSTANCE == null) {
            INSTANCE = new LoadBalancerConfigurationProvider();
        }
        return INSTANCE;
    }
}
