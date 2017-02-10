package weloveclouds.client.monitoring;

import static app_kvClient.KVClient.CLIENT_NAME;

import java.util.Arrays;

import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.monitoring.models.Service;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;

/**
 * Metric monitoring utility class for KVClient.
 * 
 * @author Hunton
 */
public class KVClientMonitoringMetricUtils {
    private static final IStatsdClient MONITORING_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

    /**
     * Records an execution time of the respective command. The name of the metric is constructed as
     * follows: {{@link app_kvClient.KVClient.clientName}}.{commandName}.{durationName}
     */
    public static void recordExecutionTime(String commandName, String durationName,
            Duration executionTime) {
        MONITORING_CLIENT.recordExecutionTime(
                new Metric.Builder().service(Service.KV_CLIENT)
                        .name(Arrays.asList(CLIENT_NAME, commandName, durationName)).build(),
                executionTime);
    }
}
