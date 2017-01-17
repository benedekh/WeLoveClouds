package weloveclouds.commons.exceptions;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents an illegal access exception.
 * 
 * @author Benedek
 */
public class IllegalAccessException extends RuntimeException {

    private static final long serialVersionUID = -7101519416680874227L;

    /**
     * The method {@link methodName} was illegally accessed in state ({@link status}).
     */
    public IllegalAccessException(String status, String methodName) {
        super(StringUtils.join(" ", "Status (", status, ") does not allow to access (", methodName,
                ") method."));
    }

}
