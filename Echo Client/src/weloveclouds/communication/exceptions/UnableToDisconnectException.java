package weloveclouds.communication.exceptions;

import weloveclouds.communication.models.RemoteServer;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UnableToDisconnectException extends ClientSideException{
    public UnableToDisconnectException() {
        super("Unable to disconnect from server.");
    }
}
