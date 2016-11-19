package weloveclouds.server.models.requests.kvserver;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
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
        return new KVTransferMessage.Builder().status(StatusType.TRANSFER_ERROR)
                .responseMessage(errorMessage).build();
    }

}
