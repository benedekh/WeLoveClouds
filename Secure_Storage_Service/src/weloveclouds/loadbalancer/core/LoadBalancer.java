package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.status.ServerStatus;
import weloveclouds.loadbalancer.services.ClientRequestInterceptorService;
import weloveclouds.loadbalancer.services.EcsNotificationService;
import weloveclouds.loadbalancer.services.HealthMonitoringService;
import weloveclouds.commons.jetty.WebService;

import static weloveclouds.commons.status.ServerStatus.HALTED;
import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-03.
 */
@Singleton
public class LoadBalancer implements ILoadBalancer {
    private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class);
    private ClientRequestInterceptorService clientRequestInterceptorService;
    private HealthMonitoringService healthMonitoringService;
    private EcsNotificationService ecsNotificationService;
    private WebService webService;
    private ServerStatus status;

    @Inject
    public LoadBalancer(ClientRequestInterceptorService clientRequestHandler,
                        HealthMonitoringService healthMonitoringService,
                        EcsNotificationService ecsNotificationService,
                        WebService webService) {
        this.status = HALTED;
        this.clientRequestInterceptorService = clientRequestHandler;
        this.healthMonitoringService = healthMonitoringService;
        this.ecsNotificationService = ecsNotificationService;
        this.webService = webService;
    }

    public ServerStatus getStatus() {
        return status;
    }

    @Override
    public void start() throws ServerSideException {
        LOGGER.info("Starting load balancer services...");

        LOGGER.debug("Starting client requests interceptor.");
        clientRequestInterceptorService.start();

        LOGGER.debug("Starting health monitoring service.");
        healthMonitoringService.start();

        LOGGER.debug("Starting ECS notification service.");
        ecsNotificationService.start();

        LOGGER.debug("Starting web service");
        webService.start();

        status = RUNNING;
        LOGGER.info("Load balancer is running.");
    }
}
