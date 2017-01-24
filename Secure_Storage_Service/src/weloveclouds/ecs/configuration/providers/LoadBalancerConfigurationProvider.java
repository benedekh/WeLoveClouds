package weloveclouds.ecs.configuration.providers;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import weloveclouds.commons.configuration.annotations.LoadBalancerDnsName;
import weloveclouds.ecs.exceptions.configuration.InvalidLoadBalancerConfigurationException;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfiguration;

/**
 * Created by Benoit on 2017-01-18.
 */
@Singleton
public class LoadBalancerConfigurationProvider {
    private static final String LOADBALANCER_PROPERTIES_FILE_PATH = "./loadbalancer.properties";
    private static final String ECS_NOTIFICATION_SERVICE_PORT_PROPERTIES =
            "ecsNotificationServicePort";
    private static final String CLIENT_REQUEST_INTERCEPTOR_PORT_PROPERTIES = "clientRequestInterceptorPort";
    private static final String HEALTH_MONITORING_SERVICE_PORT_PROPERTIES =
            "healthMonitoringServicePort";

    private Properties properties;
    private String loadBalancerDnsName;
    private LoadBalancerConfiguration loadBalancerConfiguration;

    @Inject
    public LoadBalancerConfigurationProvider(@LoadBalancerDnsName String loadBalancerDnsName)
            throws IOException, InvalidLoadBalancerConfigurationException {
        this.loadBalancerDnsName = loadBalancerDnsName;
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
                    .host(loadBalancerDnsName)
                    .ecsNotificationServicePort(Integer.parseInt(properties.getProperty
                            (ECS_NOTIFICATION_SERVICE_PORT_PROPERTIES)))
                    .healthMonitoringServicePort(Integer.parseInt(properties.getProperty
                            (HEALTH_MONITORING_SERVICE_PORT_PROPERTIES)))
                    .clientRequestInterceptorPort(Integer.parseInt(properties.getProperty
                            (CLIENT_REQUEST_INTERCEPTOR_PORT_PROPERTIES)))
                    .ecsNotificationResponsePort(NotificationServiceConfigurationProvider.getNotificationServicePort())
                    .build();
        } catch (Exception e) {
            throw new InvalidLoadBalancerConfigurationException();
        }
    }
}
