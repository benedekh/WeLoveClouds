package weloveclouds.commons.networking.models.requests;

/**
 * Common abstraction for the different request factories.
 * 
 * @author Benedek
 *
 * @param <M> the type of the message the server accepts
 * @param <R> the type of the request which shall be created from M
 */
public interface IRequestFactory<M, R extends IExecutable<M> & IValidatable<R>> {

    /**
     * @param receivedMessage the message that shall be progressed
     * @param callbackRegister where a callback can be registered which will be executed AFTER
     *        sending the <R> response message to the recipient
     * @return the response message
     */
    R createRequestFromReceivedMessage(M receivedMessage, ICallbackRegister callbackRegister);

}
