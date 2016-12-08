package weloveclouds.server.models.requests.kvecs;


import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

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
import weloveclouds.server.models.requests.kvecs.utils.StorageUnitsTransporter;
import weloveclouds.server.models.requests.kvecs.utils.StorageUnitsTransporterFactory;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A data copy request to the {@link IMovableDataAccessService}, which copies a range of the data
 * stored on the data access service to a remote location.
 * 
 * @author Benedek
 */
public class CopyDataToDestination implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(CopyDataToDestination.class);

    private IMovableDataAccessService dataAccessService;

    private ICommunicationApi communicationApi;
    private RingMetadataPart targetServerInfo;
    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;
    private StorageUnitsTransporterFactory storageUnitsTransporterFactory;

    /**
     * @param dataAccessService a reference to the data access service
     * @param targetServerInfo which contains the <IP, port> and <hash range> information about the
     *        target server to which those entries shall be transferred whose key's are in the range
     *        defined by this object
     * @param communicationApi to communicate with the target server
     * @param transferMessageSerializer to serialize {@link KVTransferMessage} into
     *        {@link SerializedMessage}
     * @param transferMessageDeserializer to deserialize {@link KVTransferMessage} from
     *        {@link SerializedMessage}
     * @param storageUnitsTransporterFactory a factory to create {@link StorageUnitsTransporter}
     */
    public CopyDataToDestination(IMovableDataAccessService dataAccessService,
            ICommunicationApi communicationApi, RingMetadataPart targetServerInfo,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer,
            StorageUnitsTransporterFactory storageUnitsTransporterFactory) {
        this.dataAccessService = dataAccessService;
        this.targetServerInfo = targetServerInfo;
        this.communicationApi = communicationApi;
        this.transferMessageSerializer = transferMessageSerializer;
        this.transferMessageDeserializer = transferMessageDeserializer;
        this.storageUnitsTransporterFactory = storageUnitsTransporterFactory;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing copy data request.");
            HashRange hashRange = targetServerInfo.getRange();
            MovableStorageUnits filteredEntries = dataAccessService.filterEntries(hashRange);

            if (!filteredEntries.getStorageUnits().isEmpty()) {
                StorageUnitsTransporter storageUnitsTransporter =
                        storageUnitsTransporterFactory.createStorageUnitsTransporter(
                                communicationApi, targetServerInfo.getConnectionInfo(),
                                transferMessageSerializer, transferMessageDeserializer);
                storageUnitsTransporter.transferStorageUnits(filteredEntries.getStorageUnits());
                LOGGER.debug("Copy data request finished successfully.");
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return new KVAdminMessage.Builder().status(RESPONSE_SUCCESS).build();
    }

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder().status(RESPONSE_ERROR).responseMessage(errorMessage)
                .build();
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

}
