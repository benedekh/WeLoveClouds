package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import weloveclouds.loadbalancer.services.ClientRequestInterceptorService;
import weloveclouds.loadbalancer.services.DistributedSystemAccessService;
import weloveclouds.loadbalancer.services.HealthMonitoringService;

/**
 * Created by Benoit on 2016-12-03.
 */
public class Loadbalancer implements ILoadbalancer {
    private ClientRequestInterceptorService clientRequestHandler;
    private HealthMonitoringService healthMonitoringService;
    private DistributedSystemAccessService distributedSystemAccessService;

    @Inject
    public Loadbalancer(ClientRequestInterceptorService clientRequestHandler,
                        DistributedSystemAccessService distributedSystemAccessService,
                        HealthMonitoringService healthMonitoringService) {
        this.clientRequestHandler = clientRequestHandler;
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.healthMonitoringService = healthMonitoringService;
    }


    @Override
    public void start() {
        clientRequestHandler.start();
        healthMonitoringService.start();
    }
}
