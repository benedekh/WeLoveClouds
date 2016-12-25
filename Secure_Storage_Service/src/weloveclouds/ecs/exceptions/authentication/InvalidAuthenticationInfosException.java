package weloveclouds.ecs.exceptions.authentication;


import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-11-17.
 */
public class InvalidAuthenticationInfosException extends ClientSideException {

    public InvalidAuthenticationInfosException() {
        super("Invalid authentication infos. A username and a password and/or private key should " +
                "be provided");
    }

    public InvalidAuthenticationInfosException(String message) {
        super(message);
    }
}
