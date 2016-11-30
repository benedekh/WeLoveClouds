package weloveclouds.commons.communication.resend.strategy;

import weloveclouds.commons.communication.backoff.BackoffInterval;
import weloveclouds.commons.communication.backoff.ExponentialBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;

public class ExponentialBackoffResendStrategyFactory {

    private static final BackoffInterval MINIMAL_INTERVAL = new BackoffInterval(300);

    public ExponentialBackoffResend createExponentialBackoffResendStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet) {
        return new ExponentialBackoffResend(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer(MINIMAL_INTERVAL));
    }

    public ExponentialBackoffResend createExponentialBackoffResendStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet, BackoffInterval minimalInterval) {
        return new ExponentialBackoffResend(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer(minimalInterval));
    }

    public ExponentialBackoffResendWithResponse createExponentialBackoffResendWithResponseStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet) {
        return new ExponentialBackoffResendWithResponse(maxNumberOfAttempts, communicationApi,
                packet, new ExponentialBackoffIntervalComputer(MINIMAL_INTERVAL));
    }

    public ExponentialBackoffResendWithResponse createExponentialBackoffResendWithResponseStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet,
            BackoffInterval minimalInterval) {
        return new ExponentialBackoffResendWithResponse(maxNumberOfAttempts, communicationApi,
                packet, new ExponentialBackoffIntervalComputer(minimalInterval));
    }
}
