package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.kvstore.models.IKVMessage.StatusType.PUT_SUCCESS;

import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Put implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;
    private String newValue;

    public Put(IDataAccessService dataAccessService, String key, String newValue) {
        this.dataAccessService = dataAccessService;
        this.key = key;
        this.newValue = newValue;
    }

    @Override
    public KVMessage execute() {
        KVMessage response;

        try {
            dataAccessService.putEntry(new KVEntry(key, newValue));
            response = new KVMessage.KVMessageBuilder().status(PUT_SUCCESS).key(key).value(newValue)
                    .build();
        } catch (StorageException e) {
            response = new KVMessage.KVMessageBuilder().status(PUT_ERROR).key(key).value(newValue)
                    .build();
        }
        return response;
    }
}
