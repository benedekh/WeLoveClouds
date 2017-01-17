package weloveclouds.commons.exceptions;

/**
 * An exception that is thrown if the respective request was invalid.
 * 
 * @author Benedek
 */
public class IllegalRequestException extends IllegalArgumentException {

    private static final long serialVersionUID = -5260690174872464195L;

    private Object response;

    public IllegalRequestException(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }
}
