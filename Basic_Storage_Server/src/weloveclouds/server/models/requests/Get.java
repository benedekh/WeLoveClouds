package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.IKVMessage.StatusType.GET_ERROR;
import static weloveclouds.kvstore.models.IKVMessage.StatusType.GET_SUCCESS;

import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Get implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;

    public Get(IDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
    }

    @Override
    public KVMessage execute() {
        KVMessage response;
        try {
            response = createResponse(GET_SUCCESS, key, dataAccessService.getValue(key));
        } catch (StorageException e) {
            response = createResponse(GET_ERROR, key, e.getMessage());
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
    }
}
