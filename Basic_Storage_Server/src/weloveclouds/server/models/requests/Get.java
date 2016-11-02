package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.IKVMessage.StatusType.GET_ERROR;
import static weloveclouds.kvstore.models.IKVMessage.StatusType.GET_SUCCESS;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Get implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;

    private Logger logger;

    public Get(IDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            logger.debug(CustomStringJoiner.join(" ", "Trying to get value for key", key));
            response = createResponse(GET_SUCCESS, key, dataAccessService.getValue(key));
        } catch (StorageException e) {
            response = createResponse(GET_ERROR, key, e.getMessage());
        } finally {
            logger.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
    }
}
