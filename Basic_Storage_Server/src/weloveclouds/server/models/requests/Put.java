package weloveclouds.server.models.requests;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

import static weloveclouds.kvstore.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.kvstore.IKVMessage.StatusType.PUT_SUCCESS;

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
        KVMessage response = null;

        try {
            dataAccessService.putEntry(new KVEntry(key, newValue));
            response = new KVMessage.KVMessageBuilder()
                    .status(PUT_SUCCESS)
                    .key(key)
                    .value(newValue)
                    .build();
        } catch (StorageException e) {
            response = new KVMessage.KVMessageBuilder()
                    .status(PUT_ERROR)
                    .key(key)
                    .value(newValue)
                    .build();
        }
        return response;
    }
}
