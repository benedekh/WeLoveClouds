package weloveclouds.commons.communication.backoff;

import org.joda.time.Duration;

public interface IBackoffIntervalComputer {

    Duration computeIntervalFrom(int attemptNumber);
}
