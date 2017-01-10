package weloveclouds.commons.monitoring.statsd;

import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;

/**
 * Represents a statistical metric collector client.
 * 
 * @author Benoit
 */
public interface IStatsdClient {
    int SINGLE_EVENT = 1;

    void incrementCounter(Metric metric);

    void incrementCounter(Metric metric, long value);

    void recordExecutionTime(Metric metric, Duration executionTime);

    void recordGaugeValue(Metric metric, double value);
}
