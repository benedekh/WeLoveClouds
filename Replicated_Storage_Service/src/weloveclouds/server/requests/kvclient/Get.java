package weloveclouds.server.requests.kvclient;

import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.GET_ERROR;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.GET_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.MovableDataAccessService;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsStoppedException;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A get request to get the respective value for a key, stored in the
 * {@link MovableDataAccessService}.
 * 
 * @author Benoit
 */

public class Get implements IKVClientRequest {

    private static final Logger LOGGER = Logger.getLogger(Get.class);

    private IMovableDataAccessService dataAccessService;
    private String key;

    private ISerializer<String, RingMetadata> ringMetadataSerializer;

    public Get(IMovableDataAccessService dataAccessService, String key,
            ISerializer<String, RingMetadata> ringMetadataSerializer) {
        this.dataAccessService = dataAccessService;
        this.key = key;
        this.ringMetadataSerializer = ringMetadataSerializer;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            LOGGER.debug(CustomStringJoiner.join(" ", "Trying to get value for key", key));
            response = createResponse(GET_SUCCESS, key, dataAccessService.getValue(key));
        } catch (KeyIsNotManagedByServiceException ex) {
            RingMetadata ringMetadata = dataAccessService.getRingMetadata();
            String ringMetadataStr = ringMetadataSerializer.serialize(ringMetadata);
            response = createResponse(SERVER_NOT_RESPONSIBLE, key, ringMetadataStr);
        } catch (ServiceIsStoppedException ex) {
            response = createResponse(SERVER_STOPPED, key, null);
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

    @Override
    public IKVClientRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateValueAsKVKey(key);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Key is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createResponse(GET_ERROR, key, errorMessage));
        }
        return this;
    }
}
