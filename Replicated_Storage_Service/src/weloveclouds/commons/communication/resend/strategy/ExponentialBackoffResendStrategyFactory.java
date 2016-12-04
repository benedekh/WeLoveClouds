package weloveclouds.commons.communication.resend.strategy;

import org.joda.time.Duration;

import weloveclouds.commons.communication.backoff.ExponentialBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;

/**
 * A factory which creates {@param ExponentialBackoffResendStrategy} or
 * {@param ExponentialBackoffResendWithResponseStrategy} instances with various parameterizations.
 * 
 * @author Benedek
 */
public class ExponentialBackoffResendStrategyFactory {

    private static final Duration MINIMAL_INTERVAL = new Duration(300);

    /**
     * Creates an {@link ExponentialBackoffResendStrategy} with {@link #MINIMAL_INTERVAL} as minimal
     * interval between the resends.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     */
    public ExponentialBackoffResendStrategy createExponentialBackoffResendStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet) {
        return createExponentialBackoffResendStrategy(maxNumberOfAttempts, communicationApi, packet,
                MINIMAL_INTERVAL);
    }

    /**
     * Creates an {@link ExponentialBackoffResendStrategy} with the respective parameterization.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     * @param minimalInterval how much time shall elapse between two resend attempts
     */
    public ExponentialBackoffResendStrategy createExponentialBackoffResendStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet,
            Duration minimalInterval) {
        return new ExponentialBackoffResendStrategy(maxNumberOfAttempts, communicationApi, packet,
                new ExponentialBackoffIntervalComputer(minimalInterval));
    }

    /**
     * Creates an {@link ExponentialBackoffResendWithResponseStrategy} with
     * {@link #MINIMAL_INTERVAL} as minimal interval between the resends.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     */
    public ExponentialBackoffResendWithResponseStrategy createExponentialBackoffResendWithResponseStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet) {
        return createExponentialBackoffResendWithResponseStrategy(maxNumberOfAttempts,
                communicationApi, packet, MINIMAL_INTERVAL);
    }

    /**
     * Creates an {@link ExponentialBackoffResendWithResponseStrategy} with the respective
     * parameterization.
     * 
     * @param maxNumberOfAttempts maximal number of attempts for resend
     * @param communicationApi the communication channel through which the packet shall be sent
     * @param packet that has to be sent over the network
     * @param minimalInterval how much time shall elapse between two resend attempts
     */
    public ExponentialBackoffResendWithResponseStrategy createExponentialBackoffResendWithResponseStrategy(
            int maxNumberOfAttempts, ICommunicationApi communicationApi, byte[] packet,
            Duration minimalInterval) {
        return new ExponentialBackoffResendWithResponseStrategy(maxNumberOfAttempts,
                communicationApi, packet, new ExponentialBackoffIntervalComputer(minimalInterval));
    }
}
