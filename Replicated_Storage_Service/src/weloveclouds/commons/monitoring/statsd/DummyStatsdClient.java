package weloveclouds.commons.monitoring.statsd;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.utils.StringUtils;

/**
 * Created by Benoit on 2016-11-27.
 */
public class DummyStatsdClient implements IStatsdClient {
    private static Logger LOGGER = Logger.getLogger(DummyStatsdClient.class);

    @Override
    public void incrementCounter(Metric metric) {
        LOGGER.info(StringUtils.join(" ", metric, "has been incremented."));
    }

    @Override
    public void incrementCounter(Metric metric, long value) {
        LOGGER.info(StringUtils.join(" ", metric, "has been incremented by:", value));
    }

    @Override
    public void recordExecutionTime(Metric metric, Duration executionTime) {
        LOGGER.info(StringUtils.join(" ", "Execution time recorded for:", metric, "with value:",
                executionTime.getMillis()));
    }

    @Override
    public void recordGaugeValue(Metric metric, double value) {
        LOGGER.info(
                StringUtils.join(" ", "Gauge value recorded for:", metric, "with value:", value));
    }
}
