package weloveclouds.commons.retryer;

import static weloveclouds.commons.utils.StringUtils.join;

import java.util.Random;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

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

    public ExponentialBackoffIntervalComputer(Duration minimalInterval) {
        this.minimalInterval = minimalInterval;
        this.maxAttemptNumber =
                (int) Math.ceil(Math.log(MAX_INTERVAL.getMillis() / minimalInterval.getMillis()));
        this.numberGenerator = new Random();
    }

    @Override
    public Duration computeIntervalFrom(int attemptNumber) {
        if (attemptNumber >= maxAttemptNumber) {
            return MAX_INTERVAL;
        }

        int powerOfTwo = (int) Math.round(Math.max(2, Math.pow(2, attemptNumber)));
        int drawnFactor = Math.max(1, numberGenerator.nextInt(powerOfTwo - 1));
        long intervalLength = drawnFactor * minimalInterval.getMillis();

        LOGGER.debug(join("", "Interval in milliseconds: ", String.valueOf(intervalLength)));

        return new Duration(intervalLength);
    }

}
