package weloveclouds.communication.api;

import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.Request;
import weloveclouds.communication.models.Response;

/**
 * @author Benoit, Benedek
 */
public interface ICommunicationApiV2 extends ICommunicationApi {

  void sendRequest(Request request) throws UnableToSendRequestToServerException;

  Response receiveResponse() throws ConnectionClosedException;
}
