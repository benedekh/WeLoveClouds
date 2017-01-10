package weloveclouds.server.requests.kvserver.transfer;

import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createErrorKVTransferMessage;

import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;

/**
 * An unrecognized request to the data access layer.
 * 
 * @author Benedek
 */
public class DefaultRequest implements IKVTransferRequest {

    private String errorMessage;

    public DefaultRequest(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public KVTransferMessage execute() {
        return createErrorKVTransferMessage(errorMessage);
    }

    @Override
    public IKVTransferRequest validate() throws IllegalArgumentException {
        return this;
    }

}
