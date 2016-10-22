package weloveclouds.communication.exceptions;

/**
 * @author Benoit, Benedek
 */
public class UnableToSendRequestToServerException extends ClientSideException {

  private static final long serialVersionUID = -7672481473679866769L;

  public UnableToSendRequestToServerException() {
    super("Unable to send request to server.");
  }

  public UnableToSendRequestToServerException(String message) {
    super(message);
  }
}
