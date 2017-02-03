package weloveclouds.commons.monitoring.statsd;


import com.timgroup.statsd.StatsDClient;

import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;

/**
 * A simply statsd client which forwards the statistical metrics via {@link #statsDClient}.
 * 
 * @author Benoit
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
    public synchronized void incrementCounter(Metric metric, long value) {
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
