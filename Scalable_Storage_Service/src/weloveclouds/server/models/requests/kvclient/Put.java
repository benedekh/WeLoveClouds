package weloveclouds.server.models.requests.kvclient;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_UPDATE;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_WRITE_LOCK;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.exceptions.WriteLockIsActiveException;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A put request to store a key and a value in the {@link DataAccessService}.
 * 
 * @author Benoit
 */
public class Put implements IKVClientRequest {

    private static final Logger LOGGER = Logger.getLogger(Put.class);

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
            LOGGER.debug(CustomStringJoiner.join(" ", "Trying to put record", key, value));

            PutType putType = dataAccessService.putEntry(new KVEntry(key, value));
            switch (putType) {
                case INSERT:
                    response = createResponse(PUT_SUCCESS, key, value);
                    break;
                case UPDATE:
                    response = createResponse(PUT_UPDATE, key, value);
                    break;
            }
        } catch (KeyIsNotManagedByServiceException ex) {
            response = createResponse(SERVER_NOT_RESPONSIBLE, key, ex.getMessage());
        } catch (ServiceIsStoppedException ex) {
            response = createResponse(SERVER_STOPPED, key, null);
        } catch (WriteLockIsActiveException ex) {
            response = createResponse(SERVER_WRITE_LOCK, key, null);
        } catch (StorageException ex) {
            response = createResponse(PUT_ERROR, key, ex.getMessage());
        } finally {
            LOGGER.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.Builder().status(status).key(key).value(value).build();
    }
}
