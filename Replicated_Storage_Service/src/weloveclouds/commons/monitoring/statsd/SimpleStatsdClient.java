package weloveclouds.commons.monitoring.statsd;


import com.timgroup.statsd.StatsDClient;

import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;

/**
 * Created by Benoit on 2016-11-27.
 */
public class SimpleStatsdClient implements IStatsdClient {
    private StatsDClient statsDClient;

    public SimpleStatsdClient(StatsDClient statsDClient) {
        this.statsDClient = statsDClient;
    }

    @Override
    synchronized public void incrementCounter(Metric metric) {
        statsDClient.incrementCounter(metric.toString());
    }

    @Override
    public void incrementCounter(Metric metric, long value) {
        statsDClient.count(metric.toString(), value);
    }

    @Override
    synchronized public void recordExecutionTime(Metric metric, Duration executionTime) {
        statsDClient.recordExecutionTime(metric.toString(), executionTime.getMillis());
    }

    @Override
    synchronized public void recordGaugeValue(Metric metric, double value) {
        statsDClient.recordGaugeValue(metric.toString(), value);
    }
}
