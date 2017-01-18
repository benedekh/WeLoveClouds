package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import weloveclouds.loadbalancer.services.ClientRequestInterceptorService;
import weloveclouds.loadbalancer.services.EcsNotificationService;
import weloveclouds.loadbalancer.services.HealthMonitoringService;

/**
 * Created by Benoit on 2016-12-03.
 */
public class LoadBalancer implements ILoadBalancer {
    private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class);
    private ClientRequestInterceptorService clientRequestInterceptorService;
    private HealthMonitoringService healthMonitoringService;
    private EcsNotificationService ecsNotificationService;

    @Inject
    public LoadBalancer(ClientRequestInterceptorService clientRequestHandler,
                        HealthMonitoringService healthMonitoringService,
                        EcsNotificationService ecsNotificationService) {
        this.clientRequestInterceptorService = clientRequestHandler;
        this.healthMonitoringService = healthMonitoringService;
        this.ecsNotificationService = ecsNotificationService;
    }

    @Override
    public void start() {
        LOGGER.info("Starting load balancer services...");

        LOGGER.debug("Starting client requests interceptor.");
        clientRequestInterceptorService.start();

        LOGGER.debug("Starting health monitoring service.");
        healthMonitoringService.start();

        LOGGER.debug("Starting ECS notification service.");
        ecsNotificationService.start();

        LOGGER.info("Load balancer is running.");
    }
}
