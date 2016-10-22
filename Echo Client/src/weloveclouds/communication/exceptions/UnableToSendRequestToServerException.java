package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UnableToSendRequestToServerException extends ClientSideException {

  private static final long serialVersionUID = -7672481473679866769L;

  public UnableToSendRequestToServerException() {
    super("Unable to send request to server.");
  }
}
