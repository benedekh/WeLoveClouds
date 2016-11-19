package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.GET_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.GET_SUCCESS;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A get request to get the respective value for a key, stored in the {@link DataAccessService}.
 * 
 * @author Benoit
 */
public class Get implements IRequest {

    private static final Logger LOGGER = Logger.getLogger(Get.class);

    private IDataAccessService dataAccessService;
    private String key;

    public Get(IDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            LOGGER.debug(CustomStringJoiner.join(" ", "Trying to get value for key", key));
            response = createResponse(GET_SUCCESS, key, dataAccessService.getValue(key));
        } catch (StorageException e) {
            response = createResponse(GET_ERROR, key, e.getMessage());
        } finally {
            LOGGER.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.Builder().status(status).key(key).value(value).build();
    }
}
