package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.loadbalancer.configuration.annotations.HealthReportingThreshold;
import weloveclouds.loadbalancer.configuration.annotations.HealthWatcherInterval;
import weloveclouds.loadbalancer.models.EcsNotification;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.status.ServiceStatus.HALTED;
import static weloveclouds.commons.status.ServiceStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-21.
 */
public class NodeHealthWatcher extends Thread {
    private Duration healthWatcherRunInterval;
    private Duration healthReportingThreshold;

    private ServiceStatus status;
    private IEcsNotificationService ecsNotificationService;
    private Map<String, Instant> heartbeatHistory;
    private ReentrantReadWriteLock heartbeatHistoryLock;

    @Inject
    public NodeHealthWatcher(IEcsNotificationService ecsNotificationService,
                             @HealthWatcherInterval Duration healthWatcherRunInterval,
                             @HealthReportingThreshold Duration healthReportingThreshold) {
        this.ecsNotificationService = ecsNotificationService;
        this.heartbeatHistory = new LinkedHashMap<>();
        this.heartbeatHistoryLock = new ReentrantReadWriteLock();
        this.healthWatcherRunInterval = healthWatcherRunInterval;
        this.healthReportingThreshold = healthReportingThreshold;
    }

    public void start() {
        this.status = RUNNING;
    }

    public void kill() {
        this.status = HALTED;
    }

    public void registerHeartbeat(NodeHealthInfos nodeHealthInfos) {
        try {
            heartbeatHistoryLock.writeLock().lock();
            heartbeatHistory.put(nodeHealthInfos.getNodeName(), Instant.now());
        } finally {
            heartbeatHistoryLock.writeLock().unlock();
        }
    }

    public void run() {
        while (status == RUNNING) {
            EcsNotification.Builder ecsNotificationBuilder = new EcsNotification.Builder();
            try {
                heartbeatHistoryLock.readLock().lock();
                for (Map.Entry<String, Instant> heartbeat : heartbeatHistory.entrySet()) {
                    Duration timeSinceLastBeat = new Duration(heartbeat.getValue(), Instant.now());
                    if (timeSinceLastBeat.isLongerThan(healthReportingThreshold)) {
                        ecsNotificationBuilder.addUnrespondingNodeName(heartbeat.getKey());
                    }
                }
            } finally {
                heartbeatHistoryLock.readLock().unlock();
                ecsNotificationService.notify(ecsNotificationBuilder.build());
                try {
                    sleep(healthWatcherRunInterval.getMillis());
                } catch (InterruptedException e) {
                    kill();
                }
            }
        }
    }
}
