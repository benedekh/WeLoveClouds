package weloveclouds.server.core.requests;

/**
 * Common abstraction for the different request factories.
 * 
 * @author Benedek
 *
 * @param <M> the type of the message the server accepts
 * @param <R> the type of the request which shall be created from M
 */
public interface IRequestFactory<M, R extends IExecutable<M> & IValidatable<R>> {

    R createRequestFromReceivedMessage(M receivedMessage);

}
