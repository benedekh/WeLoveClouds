package weloveclouds.communication.exceptions;

/**
 * Client is already disconnected from the server.
 * 
 * @author Benoit
 */
public class AlreadyDisconnectedException extends ClientSideException {

    private static final long serialVersionUID = 8571162644897670733L;

    public AlreadyDisconnectedException(String message) {
        super(message);
    }
}
