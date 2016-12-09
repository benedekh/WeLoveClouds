package weloveclouds.server.requests.kvserver;

import static weloveclouds.server.requests.kvserver.utils.KVTransferMessageFactory.createErrorKVTransferMessage;

import weloveclouds.kvstore.models.messages.KVTransferMessage;

/**
 * An unrecognized request to the data access layer.
 * 
 * @author Benedek
 */
public class DefaultRequest implements IKVServerRequest {

    private String errorMessage;

    public DefaultRequest(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public KVTransferMessage execute() {
        return createErrorKVTransferMessage(errorMessage);
    }

    @Override
    public IKVServerRequest validate() throws IllegalArgumentException {
        return this;
    }

}
