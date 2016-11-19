package weloveclouds.server.core.requests;

public interface IRequestFactory<M, R extends IExecutable<M>> {

    R createRequestFromReceivedMessage(M receivedMessage);

}
