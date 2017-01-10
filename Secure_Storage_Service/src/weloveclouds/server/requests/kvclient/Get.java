package weloveclouds.server.requests.kvclient;

import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.GET_ERROR;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.GET_SUCCESS;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;
import static weloveclouds.server.requests.kvclient.utils.KVMessageFactory.createKVMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.MovableDataAccessService;
import weloveclouds.server.services.datastore.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.datastore.exceptions.ServiceIsStoppedException;
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

    private ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer;

    protected Get(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.key = builder.key;
        this.ringMetadataSerializer = builder.ringMetadataSerializer;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            LOGGER.debug(StringUtils.join(" ", "Trying to get value for key", key));
            response = createKVMessage(GET_SUCCESS, key, dataAccessService.getValue(key));
        } catch (KeyIsNotManagedByServiceException ex) {
            RingMetadata ringMetadata = dataAccessService.getRingMetadata();
            String ringMetadataStr = ringMetadataSerializer.serialize(ringMetadata).toString();
            response = createKVMessage(SERVER_NOT_RESPONSIBLE, key, ringMetadataStr);
        } catch (ServiceIsStoppedException ex) {
            response = createKVMessage(SERVER_STOPPED, key, null);
        } catch (StorageException e) {
            response = createKVMessage(GET_ERROR, key, e.getMessage());
        } finally {
            LOGGER.debug(StringUtils.join(" ", "Result:", response));
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
            throw new IllegalRequestException(createKVMessage(GET_ERROR, key, errorMessage));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link Get} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IMovableDataAccessService dataAccessService;
        private String key;
        private ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer;

        public Builder dataAccessService(IMovableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder ringMetadataSerializer(
                ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer) {
            this.ringMetadataSerializer = ringMetadataSerializer;
            return this;
        }

        public Get build() {
            return new Get(this);
        }
    }
}
