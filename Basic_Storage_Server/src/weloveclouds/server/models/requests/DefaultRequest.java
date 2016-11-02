package weloveclouds.server.models.requests;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;

public class DefaultRequest implements IRequest {

    private String key;
    private String errorMessage;

    public DefaultRequest(String key, String errorMessage) {
        this.key = key;
        this.errorMessage = errorMessage;
    }

    @Override
    public KVMessage execute() {
        return new KVMessage.KVMessageBuilder().status(StatusType.DELETE_ERROR).key(key)
                .value(errorMessage).build();
    }

}
