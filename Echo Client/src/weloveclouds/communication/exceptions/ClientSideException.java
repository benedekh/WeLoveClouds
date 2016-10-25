package weloveclouds.communication.exceptions;

/**
 * @author Benoit, Benedek
 */
public class ClientSideException extends Exception {

  private static final long serialVersionUID = -1303292214386659879L;

  public ClientSideException(String message) {
    super(message);
  }

  public ClientSideException(String message, Exception cause){
    super(message, cause);
  }
}
