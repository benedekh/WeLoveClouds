package weloveclouds.communication.exceptions;

/**
 * @author Benoit, Benedek
 */
public class UnableToDisconnectException extends ClientSideException {

  private static final long serialVersionUID = -8394783345652857165L;

  public UnableToDisconnectException() {
    super("Unable to disconnect from server.");
  }
}
