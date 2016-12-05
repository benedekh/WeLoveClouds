package weloveclouds.server.models.requests.kvclient;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.DELETE_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.DELETE_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_WRITE_LOCK;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.commons.networking.models.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.exceptions.WriteLockIsActiveException;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A delete request to remove the key and its respective value stored in the
 * {@link DataAccessService}.
 * 
 * @author Benoit
 */
public class Delete implements IKVClientRequest {
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
        } catch (KeyIsNotManagedByServiceException ex) {
            response = createResponse(SERVER_NOT_RESPONSIBLE, key, ex.getMessage());
        } catch (ServiceIsStoppedException ex) {
            response = createResponse(SERVER_STOPPED, key, null);
        } catch (WriteLockIsActiveException ex) {
            response = createResponse(SERVER_WRITE_LOCK, key, null);
        } catch (StorageException e) {
            response = createResponse(DELETE_ERROR, key, e.getMessage());
        } finally {
            LOGGER.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    @Override
    public IKVClientRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateValueAsKVKey(key);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Key is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createResponse(DELETE_ERROR, key, errorMessage));
        }
        return this;
    }

    private KVMessage createResponse(StatusType status, String key, String value) {
        return new KVMessage.Builder().status(status).key(key).value(value).build();
    }
}
