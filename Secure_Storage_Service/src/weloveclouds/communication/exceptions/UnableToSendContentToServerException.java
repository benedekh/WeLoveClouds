package weloveclouds.communication.exceptions;

import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Client is not able to send anything to the server.
 *
 * @author Benoit, Benedek
 */
public class UnableToSendContentToServerException extends ClientSideException {

    private static final long serialVersionUID = -7672481473679866769L;

    public UnableToSendContentToServerException() {
        super("Unable to send request to server.");
    }

    public UnableToSendContentToServerException(String message) {
        super(message);
    }
}
