package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-21.
 */
public class ClientNotConnectedException extends ClientSideException {
    public ClientNotConnectedException(String message) {
        super(message);
    }
}
