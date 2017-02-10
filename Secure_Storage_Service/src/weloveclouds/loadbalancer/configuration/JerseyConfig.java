package weloveclouds.loadbalancer.configuration;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;

import weloveclouds.commons.configuration.InjectorHolder;

/**
 * Created by Benoit on 2017-01-23.
 */
public class JerseyConfig extends ResourceConfig {
    @Inject
    public JerseyConfig(ServiceLocator serviceLocator) {
        packages("weloveclouds.loadbalancer.rest.api");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(InjectorHolder.getInstance().getInjector());
    }
}
