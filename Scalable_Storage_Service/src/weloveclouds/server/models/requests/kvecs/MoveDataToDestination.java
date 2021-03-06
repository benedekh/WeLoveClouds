package weloveclouds.server.models.requests.kvecs;


import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.IMessageSerializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.networking.exceptions.IllegalRequestException;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.store.models.PersistedStorageUnit;

/**
 * A data move request to the {@link IMovableDataAccessService}, which moves a range of the data
 * stored on the data access service to a remote location.
 * 
 * @author Benedek
 */
public class MoveDataToDestination implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(MoveDataToDestination.class);

    /**
     * 1 entry (max.): 20 byte key, 120 kbyte value -> 140 kbyte + some java object metadata <br>
     * there are at most 100 ({@link PersistedStorageUnit#MAX_NUMBER_OF_ENTRIES}) entries in a
     * storage unit
     */
    private static final int NUMBER_OF_STORAGE_UNITS_TO_BE_TRANSFERRED_AT_ONCE = 30;

    private IMovableDataAccessService dataAccessService;
    private RingMetadataPart targetServerInfo;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;
    private ICommunicationApi communicationApi;

    /**
     * @param dataAccessService a reference to the data access service
     * @param targetServerInfo which contains the <IP, port> and <hash range> information about the
     *        target server to which those entries shall be transferred whose key's are in the range
     *        defined by this object
     * @param communicationApi to communicate with the target server
     */
    public MoveDataToDestination(IMovableDataAccessService dataAccessService,
            RingMetadataPart targetServerInfo, ICommunicationApi communicationApi,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
        this.dataAccessService = dataAccessService;
        this.targetServerInfo = targetServerInfo;
        this.communicationApi = communicationApi;
        this.transferMessageSerializer = transferMessageSerializer;
        this.transferMessageDeserializer = transferMessageDeserializer;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing move data request.");

            HashRange hashRange = targetServerInfo.getRange();
            MovableStorageUnits filteredEntries = dataAccessService.filterEntries(hashRange);

            if (!filteredEntries.getStorageUnits().isEmpty()) {
                try {
                    communicationApi.connectTo(targetServerInfo.getConnectionInfo());
                    transferStorageUnitsToTargetServer(filteredEntries.getStorageUnits());
                    removeStorageUnitsInRangeFromDataStore(hashRange);
                    LOGGER.debug("Move data request finished successfully.");
                } catch (UnableToConnectException ex) {
                    LOGGER.error(ex);
                    return createErrorKVAdminMessage(ex.getMessage());
                } finally {
                    try {
                        communicationApi.disconnect();
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return new KVAdminMessage.Builder()
                .status(weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS)
                .build();
    }

    /**
     * Transfers the respective MovableStorageUnit instances to the target server. Creates bunches
     * from those units that will be transferred together.
     * 
     * @param storageUnitsToTransferred
     * @throws UnableToSendContentToServerException
     * @throws ConnectionClosedException
     * @throws DeserializationException
     */
    private void transferStorageUnitsToTargetServer(
            Set<MovableStorageUnit> storageUnitsToTransferred)
            throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {

        Set<MovableStorageUnit> toBeTransferred = new HashSet<>();
        for (MovableStorageUnit strageUnitToBeMoved : storageUnitsToTransferred) {
            toBeTransferred.add(strageUnitToBeMoved);

            if (toBeTransferred.size() == NUMBER_OF_STORAGE_UNITS_TO_BE_TRANSFERRED_AT_ONCE) {
                transferBunchOverTheNetwork(new MovableStorageUnits(toBeTransferred));
                toBeTransferred.clear();
            }
        }

        transferBunchOverTheNetwork(new MovableStorageUnits(toBeTransferred));
    }

    /**
     * Transfers a bunch of storage units over the network to the target server.
     * 
     * @throws UnableToSendContentToServerException
     * @throws ConnectionClosedException
     * @throws DeserializationException
     */
    private void transferBunchOverTheNetwork(MovableStorageUnits storageUnits)
            throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {
        KVTransferMessage transferMessage = new KVTransferMessage.Builder()
                .status(StatusType.TRANSFER).storageUnits(storageUnits).build();
        SerializedMessage serializedMessage = transferMessageSerializer.serialize(transferMessage);

        communicationApi.send(serializedMessage.getBytes());

        KVTransferMessage response =
                transferMessageDeserializer.deserialize(communicationApi.receive());
        if (response.getStatus() == StatusType.TRANSFER_ERROR) {
            throw new UnableToSendContentToServerException(response.getResponseMessage());
        }
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

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder()
                .status(weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
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
