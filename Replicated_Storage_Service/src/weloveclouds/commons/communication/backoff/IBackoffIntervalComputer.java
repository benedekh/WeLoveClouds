package weloveclouds.commons.communication.backoff;

public interface IBackoffIntervalComputer {

    BackoffInterval computeIntervalFrom(int attemptNumber);
}
