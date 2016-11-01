package weloveclouds.communication.exceptions;

/**
 * Client is already connected to a server.
 *
 * @author Benedek
 */
public class AlreadyConnectedException extends UnableToConnectException {

    private static final long serialVersionUID = 1688945511048004943L;

    public AlreadyConnectedException() {
        super("Already connected to a server.");
    }

}
