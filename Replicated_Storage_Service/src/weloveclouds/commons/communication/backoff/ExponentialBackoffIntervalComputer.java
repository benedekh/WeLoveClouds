package weloveclouds.commons.communication.backoff;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.Random;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

public class ExponentialBackoffIntervalComputer implements IBackoffIntervalComputer {

    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffIntervalComputer.class);

    private Duration minimalInterval;
    private Random numberGenerator;

    public ExponentialBackoffIntervalComputer(Duration minimalInterval) {
        this.minimalInterval = minimalInterval;
        this.numberGenerator = new Random();
    }

    @Override
    public Duration computeIntervalFrom(int attemptNumber) {
        int powerOfTwo = (int) Math.round(Math.max(2, Math.pow(2, attemptNumber)));
        int drawnFactor = Math.max(1, numberGenerator.nextInt(powerOfTwo - 1));
        long intervalLength = drawnFactor * minimalInterval.getMillis();
        LOGGER.debug(join("", "Interval in milliseconds: ", String.valueOf(intervalLength)));

        return new Duration(intervalLength);
    }

}
