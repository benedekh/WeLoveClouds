package weloveclouds.loadbalancer.configuration.modules;

import com.google.inject.AbstractModule;

import weloveclouds.loadbalancer.services.EcsNotificationService;
import weloveclouds.loadbalancer.services.IEcsNotificationService;

/**
 * Created by Benoit on 2016-12-21.
 */
public class EcsNotificationServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IEcsNotificationService.class).to(EcsNotificationService.class);
    }
}
