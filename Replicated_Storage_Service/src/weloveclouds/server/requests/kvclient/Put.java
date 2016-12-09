package weloveclouds.server.requests.kvclient;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.PUT_UPDATE;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_WRITE_LOCK;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.MovableDataAccessService;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.exceptions.WriteLockIsActiveException;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A put request to store a key and a value in the {@link MovableDataAccessService}.
 * 
 * @author Benoit
 */
public class Put implements IKVClientRequest {

    private static final Logger LOGGER = Logger.getLogger(Put.class);

    private IMovableDataAccessService dataAccessService;
    private String key;
    private String value;

    private ISerializer<String, RingMetadata> ringMetadataSerializer;

    public Put(IMovableDataAccessService dataAccessService, String key, String value,
            ISerializer<String, RingMetadata> ringMetadataSerializer) {
        this.dataAccessService = dataAccessService;
        this.key = key;
        this.value = value;
        this.ringMetadataSerializer = ringMetadataSerializer;
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
            RingMetadata ringMetadata = dataAccessService.getRingMetadata();
            String ringMetadataStr = ringMetadataSerializer.serialize(ringMetadata);
            response = createResponse(SERVER_NOT_RESPONSIBLE, key, ringMetadataStr);
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

    @Override
    public IKVClientRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateValueAsKVKey(key);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Key is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createResponse(PUT_ERROR, key, errorMessage));
        }
        try {
            KVServerRequestsValidator.validateValueAsKVValue(key);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Value is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createResponse(PUT_ERROR, value, errorMessage));
        }

        return this;
    }
}
