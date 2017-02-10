package weloveclouds.server.monitoring;

import static weloveclouds.commons.monitoring.models.Service.KV_SERVER;
import static weloveclouds.commons.monitoring.statsd.IStatsdClient.SINGLE_EVENT;

import java.util.Arrays;

import org.joda.time.Duration;

import app_kvServer.KVServerCLIArgsRegistry;
import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;

/**
 * Metric monitoring utility class for KVServer.
 * 
 * @author Hunton
 */
public class KVServerMonitoringMetricUtils {

    private static final IStatsdClient MONITORING_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

    /**
     * Increments a counter. The name of the metric is constructed as follows:
     * {{@link KVServerCLIArgsRegistry#getServerName()}}.{moduleName}.{infix}.{status}
     */
    public static void incrementCounter(String moduleName, String infix, String status) {
        MONITORING_CLIENT
                .incrementCounter(
                        new Metric.Builder().service(KV_SERVER)
                                .name(Arrays.asList(
                                        KVServerCLIArgsRegistry.getInstance().getServerName(),
                                        moduleName, infix, status))
                                .build(),
                        SINGLE_EVENT);
    }

    /**
     * Records an execution time of the respective command. The name of the metric is constructed as
     * follows: {{@link KVServerCLIArgsRegistry#getServerName()}}.{moduleName}.{commandName}.{durationName}
     */
    public static void recordExecutionTime(String moduleName, String commandName,
            String durationName, Duration executionTime) {
        MONITORING_CLIENT.recordExecutionTime(new Metric.Builder().service(KV_SERVER)
                .name(Arrays.asList(KVServerCLIArgsRegistry.getInstance().getServerName(),
                        moduleName, commandName, durationName))
                .build(), executionTime);
    }

    /**
     * Records a gauge. The name of the metric is constructed as follows:
     * {{@link KVServerCLIArgsRegistry#getServerName()}}.{moduleName}.{infix}
     */
    public static void recordGauge(String moduleName, String infix, int value) {
        MONITORING_CLIENT.recordGaugeValue(new Metric.Builder().service(KV_SERVER).name(Arrays
                .asList(KVServerCLIArgsRegistry.getInstance().getServerName(), moduleName, infix))
                .build(), value);
    }

}
