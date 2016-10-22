package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-21.
 */
public class ClientSideException extends Exception {

  private static final long serialVersionUID = -1303292214386659879L;

  public ClientSideException(String message) {
    super(message);
  }
}
