package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.kvstore.models.IKVMessage.StatusType.PUT_SUCCESS;
import static weloveclouds.kvstore.models.IKVMessage.StatusType.PUT_UPDATE;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Put implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;
    private String value;

    public Put(IDataAccessService dataAccessService, String key, String value) {
        this.dataAccessService = dataAccessService;
        this.key = key;
        this.value = value;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            PutType putType = dataAccessService.putEntry(new KVEntry(key, value));
            switch (putType) {
                case INSERT:
                    response = createResponse(PUT_SUCCESS, key, value);
                    break;
                case UPDATE:
                    response = createResponse(PUT_UPDATE, key, value);
                    break;
            }
        } catch (StorageException e) {
            response = createResponse(PUT_ERROR, key, e.getMessage());
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
    }
}
