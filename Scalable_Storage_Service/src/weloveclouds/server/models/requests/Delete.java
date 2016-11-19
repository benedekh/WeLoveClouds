package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.DELETE_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.DELETE_SUCCESS;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A delete request to remove the key and its respective value stored in the
 * {@link DataAccessService}.
 * 
 * @author Benoit
 */
public class Delete implements IRequest {
    
    private static final Logger LOGGER = Logger.getLogger(Delete.class);
    
    private IDataAccessService dataAccessService;
    private String key;

    public Delete(IDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            LOGGER.debug(CustomStringJoiner.join(" ", "Trying to remove key", key));
            dataAccessService.removeEntry(key);
            response = createResponse(DELETE_SUCCESS, key, null);
        } catch (StorageException e) {
            response = createResponse(DELETE_ERROR, key, e.getMessage());
        } finally {
            LOGGER.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.Builder().status(status).key(key).value(value).build();
    }
}
