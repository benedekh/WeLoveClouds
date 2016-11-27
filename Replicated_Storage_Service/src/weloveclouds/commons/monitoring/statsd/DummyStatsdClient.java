package weloveclouds.commons.monitoring.statsd;

import org.apache.log4j.Logger;
import org.joda.time.Duration;


import weloveclouds.commons.monitoring.models.Metric;

import static weloveclouds.client.utils.CustomStringJoiner.*;

/**
 * Created by Benoit on 2016-11-27.
 */
public class DummyStatsdClient implements IStatsdClient {
    private static Logger LOGGER = Logger.getLogger(DummyStatsdClient.class);

    @Override
    public void incrementCounter(Metric metric) {
        LOGGER.info(join(" ", metric.toString(), "has been incremented."));
    }

    @Override
    public void recordExecutionTime(Metric metric, Duration executionTime) {
        LOGGER.info(join(" ", "Execution time recorded for:", metric.toString(), "with value:",
                String.valueOf(executionTime.getMillis())));
    }

    @Override
    public void recordGaugeValue(Metric metric, double value) {
        LOGGER.info(join(" ", "Gauge value recorded for:", metric.toString(), "with value:",
                String.valueOf(value)));
    }
}
