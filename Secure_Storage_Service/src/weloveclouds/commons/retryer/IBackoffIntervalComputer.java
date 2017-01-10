package weloveclouds.commons.retryer;

import org.joda.time.Duration;

/**
 * Common method for backoff interval computers which calculate the length of the backoff interval,
 * based on which attempt it is.
 * 
 * @author Benedek
 */
public interface IBackoffIntervalComputer {

    /**
     * Calculate the length of the backoff interval, based on which attempt it is.
     */
    Duration computeIntervalFrom(int attemptNumber);
}
