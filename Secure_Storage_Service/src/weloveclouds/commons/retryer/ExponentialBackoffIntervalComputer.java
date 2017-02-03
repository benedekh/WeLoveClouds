package weloveclouds.commons.retryer;

import com.google.inject.Inject;

import java.util.Random;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.configuration.annotations.MinimumIntervalBetweenRetry;
import weloveclouds.commons.utils.StringUtils;

/**
 * A backoff interval computer which calculates the length of the backoff interval, based on which
 * attempt it is.
 *
 * @author Benedek
 */
public class ExponentialBackoffIntervalComputer implements IBackoffIntervalComputer {
    private static final Duration MAX_INTERVAL = new Duration(5 * 60 * 1000);
    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffIntervalComputer.class);

    private int maxAttemptNumber;
    private Duration minimalInterval;
    private Random numberGenerator;

    @Inject
    public ExponentialBackoffIntervalComputer(@MinimumIntervalBetweenRetry Duration minimalInterval) {
        this.minimalInterval = minimalInterval;
        double orderOfMagnitude =
                MAX_INTERVAL.getMillis() / Math.max(1, minimalInterval.getMillis());
        this.maxAttemptNumber = (int) Math.ceil(Math.log10(orderOfMagnitude + 1) / Math.log10(2));
        this.numberGenerator = new Random();
    }

    @Override
    public Duration computeIntervalFrom(int attemptNumber) {
        if (attemptNumber >= maxAttemptNumber) {
            LOGGER.debug(
                    StringUtils.join("", "Interval in milliseconds: ", MAX_INTERVAL.getMillis()));
            return MAX_INTERVAL;
        }

        int powerOfTwo = (int) Math.max(2, Math.pow(2, attemptNumber));
        int drawnFactor = Math.max(1, numberGenerator.nextInt(powerOfTwo - 1));
        long intervalLength = drawnFactor * minimalInterval.getMillis();

        LOGGER.debug(StringUtils.join("", "Interval in milliseconds: ", intervalLength));

        return new Duration(intervalLength);
    }
}
