package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UnableToDisconnectException extends ClientSideException {

  private static final long serialVersionUID = -8394783345652857165L;

  public UnableToDisconnectException() {
    super("Unable to disconnect from server.");
  }
}
