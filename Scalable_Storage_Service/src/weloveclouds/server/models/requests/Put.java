package weloveclouds.server.models.requests;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_UPDATE;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A put request to store a key and a value in the {@link DataAccessService}.
 * 
 * @author Benoit
 */
public class Put implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;
    private String value;

    private Logger logger;

    public Put(IDataAccessService dataAccessService, String key, String value) {
        this.dataAccessService = dataAccessService;
        this.key = key;
        this.value = value;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            logger.debug(CustomStringJoiner.join(" ", "Trying to put record", key, value));

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
        } finally {
            logger.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.Builder().status(status).key(key).value(value).build();
    }
}
