package weloveclouds.commons.communication.backoff;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.Random;

import org.apache.log4j.Logger;

public class ExponentialBackoffIntervalComputer implements IBackoffIntervalComputer {

    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffIntervalComputer.class);
    private static final int MIN_INTERVAL_IN_MILLISECONDS = 300;

    private int numberOfAttemptsSoFar;
    private Random numberGenerator;

    public ExponentialBackoffIntervalComputer() {
        this.numberOfAttemptsSoFar = 0;
        this.numberGenerator = new Random();
    }

    @Override
    public BackoffInterval computeIntervalFrom(int attemptNumber) {
        LOGGER.info(join("", "#",
                String.valueOf(numberOfAttemptsSoFar) + " resend attempts were made."));

        int powerOfTwo = (int) Math.round(Math.max(2, Math.pow(2, numberOfAttemptsSoFar)));
        LOGGER.info(join("", "Power of two: ", String.valueOf(powerOfTwo)));

        int drawnFactor = Math.max(1, numberGenerator.nextInt(powerOfTwo - 1));
        LOGGER.info(join("", "Drawn multiplication factor: ", String.valueOf(powerOfTwo)));

        BackoffInterval interval = new BackoffInterval(drawnFactor * MIN_INTERVAL_IN_MILLISECONDS);
        LOGGER.info(interval);

        return interval;
    }

}
