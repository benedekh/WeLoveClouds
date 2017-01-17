package weloveclouds.server.requests.kvclient;

import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;

/**
 * An unrecognized request to the data access layer.
 * 
 * @author Benedek
 */
public class DefaultRequest implements IKVClientRequest {

    private String key;
    private String errorMessage;

    public DefaultRequest(String key, String errorMessage) {
        this.key = key;
        this.errorMessage = errorMessage;
    }

    @Override
    public KVMessage execute() {
        return new KVMessage.Builder().status(StatusType.DELETE_ERROR).key(key).value(errorMessage)
                .build();
    }

    @Override
    public IKVClientRequest validate() throws IllegalArgumentException {
        return this;
    }

}
