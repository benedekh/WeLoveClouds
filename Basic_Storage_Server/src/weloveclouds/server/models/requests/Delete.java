package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.IKVMessage.StatusType.DELETE_ERROR;
import static weloveclouds.kvstore.models.IKVMessage.StatusType.DELETE_SUCCESS;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Delete implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;

    public Delete(IDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
    }

    @Override
    public KVMessage execute() {
        KVMessage response;
        try {
            dataAccessService.removeEntry(key);
            response = createResponse(DELETE_SUCCESS, key, null);
        } catch (StorageException e) {
            response = createResponse(DELETE_ERROR, key, e.getMessage());
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
    }
}
