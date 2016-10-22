package weloveclouds.communication.exceptions;

/**
 * @author Benedek
 */
public class ConnectionClosedException extends ClientSideException{

  private static final long serialVersionUID = 4270051741209583154L;

  public ConnectionClosedException(){
    super("Connection was closed while waiting for response from the server.");
  }

}
