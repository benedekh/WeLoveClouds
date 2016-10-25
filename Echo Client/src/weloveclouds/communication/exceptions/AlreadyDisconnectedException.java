package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-25.
 */
public class AlreadyDisconnectedException extends ClientSideException{
    public AlreadyDisconnectedException(String message) {
        super(message);
    }
}
