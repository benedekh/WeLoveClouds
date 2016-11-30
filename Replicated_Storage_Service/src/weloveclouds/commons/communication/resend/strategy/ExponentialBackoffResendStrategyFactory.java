package weloveclouds.commons.communication.resend.strategy;

import weloveclouds.commons.communication.backoff.ExponentialBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;

public class ExponentialBackoffResendStrategyFactory {

    public ExponentialBackoffResend createExponentialBackoffResendStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet) {
        return new ExponentialBackoffResend(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer());
    }
    
    public ExponentialBackoffResendWithResponse createExponentialBackoffResendWithResponseStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet) {
        return new ExponentialBackoffResendWithResponse(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer());
    }
}
