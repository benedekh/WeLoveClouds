package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import org.joda.time.Duration;

import weloveclouds.loadbalancer.services.ClientRequestInterceptorService;
import weloveclouds.loadbalancer.services.DistributedSystemAccessService;
import weloveclouds.loadbalancer.services.EcsNotificationService;
import weloveclouds.loadbalancer.services.HealthMonitoringService;

/**
 * Created by Benoit on 2016-12-03.
 */
public class Loadbalancer implements ILoadbalancer {
    private ClientRequestInterceptorService clientRequestHandler;
    private HealthMonitoringService healthMonitoringService;
    private EcsNotificationService ecsNotificationService;

    @Inject
    public Loadbalancer(ClientRequestInterceptorService clientRequestHandler,
                        HealthMonitoringService healthMonitoringService,
                        EcsNotificationService ecsNotificationService) {
        this.clientRequestHandler = clientRequestHandler;
        this.healthMonitoringService = healthMonitoringService;
        this.ecsNotificationService = ecsNotificationService;
    }


    @Override
    public void start() {
        clientRequestHandler.start();
        healthMonitoringService.start();
        ecsNotificationService.start();
    }
}
