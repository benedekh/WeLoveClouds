package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.models.messaging.notification.KVEcsNotificationMessage;
import weloveclouds.loadbalancer.configuration.annotations.HealthReportingThreshold;
import weloveclouds.loadbalancer.configuration.annotations.HealthWatcherInterval;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.status.ServiceStatus.HALTED;
import static weloveclouds.commons.status.ServiceStatus.RUNNING;
import static weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage.Status.UNRESPONSIVE_NODES_REPORTING;

/**
 * Created by Benoit on 2016-12-21.
 */
public class NodeHealthWatcher extends Thread {
    private static final Logger LOGGER = Logger.getLogger(NodeHealthWatcher.class);
    private Duration healthWatcherRunInterval;
    private Duration healthReportingThreshold;

    private ServiceStatus status;
    private IEcsNotificationService ecsNotificationService;
    private ConcurrentHashMap<String, Instant> heartbeatHistory;

    @Inject
    public NodeHealthWatcher(IEcsNotificationService ecsNotificationService,
                             @HealthWatcherInterval Duration healthWatcherRunInterval,
                             @HealthReportingThreshold Duration healthReportingThreshold) {
        this.ecsNotificationService = ecsNotificationService;
        this.heartbeatHistory = new ConcurrentHashMap<>();
        this.healthWatcherRunInterval = healthWatcherRunInterval;
        this.healthReportingThreshold = healthReportingThreshold;
    }

    public void kill() {
        this.status = HALTED;
    }

    public void registerHeartbeat(NodeHealthInfos nodeHealthInfos) {
        heartbeatHistory.put(nodeHealthInfos.getNodeName(), Instant.now());
    }

    public void run() {
        this.status = RUNNING;
        KVEcsNotificationMessage.Builder ecsNotificationBuilder = new KVEcsNotificationMessage
                .Builder().status(UNRESPONSIVE_NODES_REPORTING);
        boolean nodeFailureDetected;
        String unresponsiveNodeName;
        while (status == RUNNING) {
            nodeFailureDetected = false;
            try {
                for (Map.Entry<String, Instant> heartbeat : heartbeatHistory.entrySet()) {
                    Duration timeSinceLastBeat = new Duration(heartbeat.getValue(), Instant.now());
                    if (timeSinceLastBeat.isLongerThan(healthReportingThreshold)) {
                        nodeFailureDetected = true;
                        unresponsiveNodeName = heartbeat.getKey();
                        LOGGER.info(StringUtils.join(" ", "Node :", unresponsiveNodeName, "failed"));
                        ecsNotificationBuilder.addUnresponsiveNodeName(unresponsiveNodeName);
                        heartbeatHistory.remove(unresponsiveNodeName);
                    }
                }
            } finally {
                if (nodeFailureDetected) {
                    ecsNotificationService.notify(ecsNotificationBuilder.build());
                    ecsNotificationBuilder.reset();
                }
                try {
                    sleep(healthWatcherRunInterval.getMillis());
                } catch (InterruptedException e) {
                    kill();
                }
            }
        }
    }
}
