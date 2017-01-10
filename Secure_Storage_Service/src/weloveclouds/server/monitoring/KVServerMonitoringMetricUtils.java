package weloveclouds.server.monitoring;

import static app_kvServer.KVServer.serverName;
import static weloveclouds.commons.monitoring.models.Service.KV_SERVER;
import static weloveclouds.commons.monitoring.statsd.IStatsdClient.SINGLE_EVENT;

import java.util.Arrays;

import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;

public class KVServerMonitoringMetricUtils {

    private static final IStatsdClient MONITORING_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

    public static void incrementCounter(String moduleName, String infix, String status) {
        MONITORING_CLIENT.incrementCounter(
                new Metric.Builder().service(KV_SERVER)
                        .name(Arrays.asList(serverName, moduleName, infix, status)).build(),
                SINGLE_EVENT);
    }

    public static void recordExecutionTime(String moduleName, String commandName,
            String durationName, Duration executionTime) {
        MONITORING_CLIENT.recordExecutionTime(new Metric.Builder().service(KV_SERVER)
                .name(Arrays.asList(serverName, moduleName, commandName, durationName)).build(),
                executionTime);
    }

    public static void recordGauge(String moduleName, String infix, int value) {
        MONITORING_CLIENT.recordGaugeValue(new Metric.Builder().service(KV_SERVER)
                .name(Arrays.asList(serverName, moduleName, infix)).build(), value);
    }

}
