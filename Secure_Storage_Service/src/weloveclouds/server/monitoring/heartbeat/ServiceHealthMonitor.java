package weloveclouds.server.monitoring.heartbeat;

import static weloveclouds.server.monitoring.KVServerMonitoringMetricUtils.recordGauge;

import java.util.concurrent.atomic.AtomicInteger;

import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

public class ServiceHealthMonitor {

    private String serviceName;
    private ServiceHealthInfos.Builder healthInfosBuilder;
    private AtomicInteger numberOfConnections;

    public ServiceHealthMonitor(ServiceHealthInfos.Builder preInitializedHealthInfosBuilder) {
        this.healthInfosBuilder = preInitializedHealthInfosBuilder;
        this.serviceName = healthInfosBuilder.build().getServiceName();
        this.numberOfConnections = new AtomicInteger();
    }

    public void incrementConnections() {
        setActiveConnections(numberOfConnections.incrementAndGet());
    }

    public void decrementConnections() {
        setActiveConnections(numberOfConnections.decrementAndGet());
    }

    public void setServiceStatus(ServiceStatus status) {
        healthInfosBuilder.serviceStatus(status);
    }

    public ServiceHealthInfos getHealthInfos() {
        return healthInfosBuilder.build();
    }

    private void setActiveConnections(int numberOfActiveConnections) {
        healthInfosBuilder.numberOfActiveConnections(numberOfActiveConnections);
        recordGauge(serviceName, "active_connections", numberOfActiveConnections);
    }

}
