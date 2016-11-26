package weloveclouds.communication.exceptions;

import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Client is not able to disconnect from th server.
 *
 * @author Benoit, Benedek
 */
public class UnableToDisconnectException extends ClientSideException {

    private static final long serialVersionUID = -8394783345652857165L;

    public UnableToDisconnectException() {
        super("Unable to disconnect from server.");
    }

    public UnableToDisconnectException(String message) {
        super(message);
    }
}
