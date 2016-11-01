package weloveclouds.communication.exceptions;

/**
 * Client is not connected to any server.
 *
 * @author Benoit, Benedek
 */
public class ClientNotConnectedException extends UnableToSendContentToServerException {

    private static final long serialVersionUID = 9039920327684454891L;

    public ClientNotConnectedException() {
        super("Client is not connected to the server.");
    }
}
