package weloveclouds.client.utils.monitoring;

import static app_kvClient.KVClient.clientName;

import java.util.Arrays;

import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.monitoring.models.Service;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;

public class KVClientMonitoringMetricUtils {
    private static final IStatsdClient MONITORING_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

    public static void recordExecutionTime(String commandName, String durationName,
            Duration executionTime) {
        MONITORING_CLIENT.recordExecutionTime(
                new Metric.Builder().service(Service.KV_CLIENT)
                        .name(Arrays.asList(clientName, commandName, durationName)).build(),
                executionTime);
    }
}
