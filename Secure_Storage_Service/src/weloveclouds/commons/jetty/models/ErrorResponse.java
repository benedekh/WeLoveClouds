package weloveclouds.commons.jetty.models;

/**
 * Created by Benoit on 2017-01-25.
 */
public class ErrorResponse {
    String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public ErrorResponse withMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
