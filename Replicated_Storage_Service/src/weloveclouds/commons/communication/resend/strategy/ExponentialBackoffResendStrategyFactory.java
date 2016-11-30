package weloveclouds.commons.communication.resend.strategy;

import org.joda.time.Duration;

import weloveclouds.commons.communication.backoff.ExponentialBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;

public class ExponentialBackoffResendStrategyFactory {

    private static final Duration MINIMAL_INTERVAL = new Duration(300);

    public ExponentialBackoffResendStrategy createExponentialBackoffResendStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet) {
        return new ExponentialBackoffResendStrategy(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer(MINIMAL_INTERVAL));
    }

    public ExponentialBackoffResendStrategy createExponentialBackoffResendStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet, Duration minimalInterval) {
        return new ExponentialBackoffResendStrategy(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer(minimalInterval));
    }

    public ExponentialBackoffResendWithResponseStrategy createExponentialBackoffResendWithResponseStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet) {
        return new ExponentialBackoffResendWithResponseStrategy(maxNumberOfAttempts, communicationApi,
                packet, new ExponentialBackoffIntervalComputer(MINIMAL_INTERVAL));
    }

    public ExponentialBackoffResendWithResponseStrategy createExponentialBackoffResendWithResponseStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet,
            Duration minimalInterval) {
        return new ExponentialBackoffResendWithResponseStrategy(maxNumberOfAttempts, communicationApi,
                packet, new ExponentialBackoffIntervalComputer(minimalInterval));
    }
}
