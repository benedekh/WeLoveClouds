package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-21.
 */
public class ClientNotConnectedException extends ClientSideException {

  private static final long serialVersionUID = 9039920327684454891L;

  public ClientNotConnectedException(String message) {
    super(message);
  }
}
