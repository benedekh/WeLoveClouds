package weloveclouds.commons.exceptions;

import weloveclouds.commons.utils.StringUtils;

public class IllegalAccessException extends RuntimeException {

    private static final long serialVersionUID = -7101519416680874227L;

    public IllegalAccessException(String status, String methodName) {
        super(StringUtils.join(" ", "Status (", status, ") does not allow to access (", methodName,
                ") method."));
    }

}
