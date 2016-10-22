package weloveclouds.communication.exceptions;

/**
 * @author Benoit, Benedek
 */
public class ClientNotConnectedException extends UnableToSendRequestToServerException {

  private static final long serialVersionUID = 9039920327684454891L;

  public ClientNotConnectedException() {
    super("Client is not connected to the server.");
  }
}
