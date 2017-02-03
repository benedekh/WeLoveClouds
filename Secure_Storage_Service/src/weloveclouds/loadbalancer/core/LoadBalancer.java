package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.jetty.IWebService;
import weloveclouds.commons.status.ServerStatus;
import weloveclouds.loadbalancer.services.IClientRequestInterceptorService;
import weloveclouds.loadbalancer.services.IEcsNotificationService;
import weloveclouds.loadbalancer.services.IHealthMonitoringService;

import static weloveclouds.commons.status.ServerStatus.HALTED;
import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-03.
 */
@Singleton
public class LoadBalancer implements ILoadBalancer {
    private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class);
    private IClientRequestInterceptorService clientRequestInterceptorService;
    private IHealthMonitoringService healthMonitoringService;
    private IEcsNotificationService ecsNotificationService;
    private IWebService webService;
    private ServerStatus status;

    @Inject
    public LoadBalancer(IClientRequestInterceptorService clientRequestInterceptorService,
                        IHealthMonitoringService healthMonitoringService,
                        IEcsNotificationService ecsNotificationService,
                        IWebService webService) {
        this.status = HALTED;
        this.clientRequestInterceptorService = clientRequestInterceptorService;
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
