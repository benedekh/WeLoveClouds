package weloveclouds.communication.exceptions;

/**
 * Created by Benoit on 2016-10-25.
 */
public class AlreadyDisconnectedException extends ClientSideException{

  private static final long serialVersionUID = 8571162644897670733L;

    public AlreadyDisconnectedException(String message) {
        super(message);
    }
}
