package weloveclouds.server.requests.kvecs;


import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.requests.kvecs.utils.StorageUnitsTransporter;
import weloveclouds.server.requests.kvecs.utils.StorageUnitsTransporterFactory;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.IReplicableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A data move request to the {@link IMovableDataAccessService}, which moves a range of the data
 * stored on the data access service to a remote location.
 * 
 * @author Benedek
 */
public class MoveDataToDestination implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(MoveDataToDestination.class);

    private IMovableDataAccessService dataAccessService;

    private ICommunicationApi communicationApi;
    private RingMetadataPart targetServerInfo;
    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;
    private StorageUnitsTransporterFactory storageUnitsTransporterFactory;

    protected MoveDataToDestination(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.targetServerInfo = builder.targetServerInfo;
        this.communicationApi = builder.communicationApi;
        this.transferMessageSerializer = builder.transferMessageSerializer;
        this.transferMessageDeserializer = builder.transferMessageDeserializer;
        this.storageUnitsTransporterFactory = builder.storageUnitsTransporterFactory;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing move data request.");
            HashRange hashRange = targetServerInfo.getWriteRange();
            MovableStorageUnits filteredEntries = dataAccessService.filterEntries(hashRange);

            if (!filteredEntries.getStorageUnits().isEmpty()) {
                StorageUnitsTransporter storageUnitsTransporter =
                        storageUnitsTransporterFactory.createStorageUnitsTransporter(
                                communicationApi, targetServerInfo.getConnectionInfo(),
                                transferMessageSerializer, transferMessageDeserializer);
                storageUnitsTransporter.transferStorageUnits(filteredEntries.getStorageUnits());
                removeStorageUnitsInRangeFromDataStore(hashRange);
                LOGGER.debug("Move data request finished successfully.");
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return createSuccessKVAdminMessage();
    }

    /**
     * Removes those storage units from the {@link IMovableDataAccessService} whose keys are in the
     * respective range.
     * 
     * @throws StorageException
     */
    private void removeStorageUnitsInRangeFromDataStore(HashRange range) throws StorageException {
        dataAccessService.removeEntries(range);
        dataAccessService.defragment();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateRingMetadataPart(targetServerInfo);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Target server information is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link MoveDataToDestination} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IReplicableDataAccessService dataAccessService;
        private RingMetadataPart targetServerInfo;
        private ICommunicationApi communicationApi;
        private StorageUnitsTransporterFactory storageUnitsTransporterFactory;
        private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
        private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

        /**
         * @param dataAccessService a reference to the data access service
         */
        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        /**
         * @param targetServerInfo which contains the <IP, port> and <hash range> information about
         *        the target server to which those entries shall be transferred whose key's are in
         *        the range defined by this object
         */
        public Builder targetServerInfo(RingMetadataPart targetServerInfo) {
            this.targetServerInfo = targetServerInfo;
            return this;
        }

        /**
         * @param communicationApi to communicate with the target server
         */
        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        /**
         * @param storageUnitsTransporterFactory a factory to create {@link StorageUnitsTransporter}
         */
        public Builder storageUnitsTransporterFactory(
                StorageUnitsTransporterFactory storageUnitsTransporterFactory) {
            this.storageUnitsTransporterFactory = storageUnitsTransporterFactory;
            return this;
        }

        /**
         * @param transferMessageSerializer to serialize {@link KVTransferMessage} into
         *        {@link SerializedMessage}
         */
        public Builder transferMessageSerializer(
                IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer) {
            this.transferMessageSerializer = transferMessageSerializer;
            return this;
        }

        /**
         * @param transferMessageDeserializer to deserialize {@link KVTransferMessage} from
         *        {@link SerializedMessage}
         */
        public Builder transferMessageDeserializer(
                IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
            this.transferMessageDeserializer = transferMessageDeserializer;
            return this;
        }

        public MoveDataToDestination build() {
            return new MoveDataToDestination(this);
        }
    }

}
