package weloveclouds.ecs.configuration.modules;

import com.google.inject.AbstractModule;

import weloveclouds.ecs.configuration.annotations.NotificationServiceMaxRetry;
import weloveclouds.ecs.configuration.annotations.NotificationServicePort;
import weloveclouds.ecs.configuration.providers.NotificationServiceConfigurationProvider;

/**
 * Created by Benoit on 2016-12-21.
 */
public class NotificationServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(NotificationServicePort.class)
                .toInstance(NotificationServiceConfigurationProvider.getNotificationServicePort());

        bind(Integer.class).annotatedWith(NotificationServiceMaxRetry.class)
                .toInstance(NotificationServiceConfigurationProvider.getNotificationServiceMaxRetryNumber());
    }
}
