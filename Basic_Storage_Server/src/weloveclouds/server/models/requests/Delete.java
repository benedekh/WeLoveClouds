package weloveclouds.server.models.requests;

import weloveclouds.kvstore.IKVMessage;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

import static weloveclouds.kvstore.IKVMessage.StatusType.DELETE_ERROR;
import static weloveclouds.kvstore.IKVMessage.StatusType.DELETE_SUCCESS;

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
        try{
            dataAccessService.removeEntry(key);
            response = new KVMessage.KVMessageBuilder()
                    .status(DELETE_SUCCESS)
                    .key(key)
                    .value(null)
                    .build();
        }catch(StorageException e){
            response = new KVMessage.KVMessageBuilder()
                    .status(DELETE_ERROR)
                    .key(key)
                    .value(null)
                    .build();
        }
        return response;
    }
}
