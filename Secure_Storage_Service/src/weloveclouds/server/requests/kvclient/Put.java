package weloveclouds.server.requests.kvclient;

import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.PUT_ERROR;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.PUT_SUCCESS;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.PUT_UPDATE;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_WRITE_LOCK;
import static weloveclouds.server.requests.kvclient.utils.KVMessageFactory.createKVMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.datastore.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.datastore.exceptions.WriteLockIsActiveException;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.PutType;

/**
 * A put request to store a key and a value in the {@link IMovableDataAccessService}.
 * 
 * @author Benoit
 */
public class Put implements IKVClientRequest {

    private static final Logger LOGGER = Logger.getLogger(Put.class);

    private IMovableDataAccessService dataAccessService;
    private String key;
    private String value;

    private ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer;

    protected Put(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.key = builder.key;
        this.value = builder.value;
        this.ringMetadataSerializer = builder.ringMetadataSerializer;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            LOGGER.debug(StringUtils.join(" ", "Trying to put record", key, value));

            PutType putType = dataAccessService.putEntry(new KVEntry(key, value));
            switch (putType) {
                case INSERT:
                    response = createKVMessage(PUT_SUCCESS, key, value);
                    break;
                case UPDATE:
                    response = createKVMessage(PUT_UPDATE, key, value);
                    break;
            }
        } catch (KeyIsNotManagedByServiceException ex) {
            RingMetadata ringMetadata = dataAccessService.getRingMetadata();
            String ringMetadataStr = ringMetadataSerializer.serialize(ringMetadata).toString();
            response = createKVMessage(SERVER_NOT_RESPONSIBLE, key, ringMetadataStr);
        } catch (ServiceIsStoppedException ex) {
            response = createKVMessage(SERVER_STOPPED, key, null);
        } catch (WriteLockIsActiveException ex) {
            response = createKVMessage(SERVER_WRITE_LOCK, key, null);
        } catch (StorageException ex) {
            response = createKVMessage(PUT_ERROR, key, ex.getMessage());
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
            throw new IllegalRequestException(createKVMessage(PUT_ERROR, key, errorMessage));
        }
        try {
            KVServerRequestsValidator.validateValueAsKVValue(key);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Value is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createKVMessage(PUT_ERROR, value, errorMessage));
        }

        return this;
    }

    /**
     * Builder pattern for creating a {@link Put} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IMovableDataAccessService dataAccessService;
        private String key;
        private String value;
        private ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer;

        public Builder dataAccessService(IMovableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder ringMetadataSerializer(
                ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer) {
            this.ringMetadataSerializer = ringMetadataSerializer;
            return this;
        }

        public Put build() {
            return new Put(this);
        }
    }
}
