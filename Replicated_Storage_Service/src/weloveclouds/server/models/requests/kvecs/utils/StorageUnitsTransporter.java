package weloveclouds.server.models.requests.kvecs.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.store.models.PersistedStorageUnit;

/**
 * Utility class which helps with transferring {@link MovableStorageUnit} instances to a remote
 * destination.
 * 
 * @author Benedek
 */
public class StorageUnitsTransporter {

    private static final Logger LOGGER = Logger.getLogger(StorageUnitsTransporter.class);

    /**
     * 1 entry (max.): 20 byte key, 120 kbyte value -> 140 kbyte + some java object metadata <br>
     * there are at most 100 ({@link PersistedStorageUnit#MAX_NUMBER_OF_ENTRIES}) entries in a
     * storage unit
     */
    private static final int NUMBER_OF_STORAGE_UNITS_TO_BE_TRANSFERRED_AT_ONCE = 30;

    private ICommunicationApi communicationApi;
    private ServerConnectionInfo connectionInfo;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

    /**
     * @param communicationApi to communicate with the target server
     * @param connectionInfo the IP + port of the destination
     * @param transferMessageSerializer to serialize {@link KVTransferMessage} into
     *        {@link SerializedMessage}
     * @param transferMessageDeserializer to deserialize {@link KVTransferMessage} from
     *        {@link SerializedMessage}
     */
    public StorageUnitsTransporter(ICommunicationApi communicationApi,
            ServerConnectionInfo connectionInfo,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
        this.communicationApi = communicationApi;
        this.connectionInfo = connectionInfo;
        this.transferMessageSerializer = transferMessageSerializer;
        this.transferMessageDeserializer = transferMessageDeserializer;
    }

    /**
     * Transfers the respective {@link MovableStorageUnit} instances respecting the limitation set
     * by {@link #NUMBER_OF_STORAGE_UNITS_TO_BE_TRANSFERRED_AT_ONCE} to the destination denoted by
     * {@link #connectionInfo}.
     * 
     * @param storageUnitsToTransferred storage units that have to be transferred to the remote
     *        destination.
     * @throws UnableToConnectException if the {@link #connectionInfo} is not contactable
     * @throws UnableToSendContentToServerException if any error occurs during the transfer
     */
    public void transferStorageUnits(Set<MovableStorageUnit> storageUnitsToTransferred)
            throws UnableToConnectException, UnableToSendContentToServerException {
        try {
            communicationApi.connectTo(connectionInfo);
            transferStorageUnitsToTargetServer(storageUnitsToTransferred);
        } finally {
            try {
                communicationApi.disconnect();
            } catch (Exception ex) {
                LOGGER.error(ex);
            }
        }
    }

    /**
     * Transfers the respective MovableStorageUnit instances to the target server. Creates bunches
     * from those units that will be transferred together.
     * 
     * @throws UnableToSendContentToServerException if an error occurs
     */
    private void transferStorageUnitsToTargetServer(
            Set<MovableStorageUnit> storageUnitsToTransferred)
            throws UnableToSendContentToServerException {
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
     * @throws UnableToSendContentToServerException if an error occurs
     */
    private void transferBunchOverTheNetwork(MovableStorageUnits storageUnits)
            throws UnableToSendContentToServerException {
        try {
            KVTransferMessage transferMessage = new KVTransferMessage.Builder()
                    .status(StatusType.TRANSFER).storageUnits(storageUnits).build();
            SerializedMessage serializedMessage =
                    transferMessageSerializer.serialize(transferMessage);

            byte[] responsePacket =
                    communicationApi.sendAndExpectForResponse(serializedMessage.getBytes());

            KVTransferMessage response = transferMessageDeserializer.deserialize(responsePacket);
            if (response.getStatus() == StatusType.TRANSFER_ERROR) {
                throw new UnableToSendContentToServerException(response.getResponseMessage());
            }
        } catch (DeserializationException | IOException ex) {
            throw new UnableToSendContentToServerException(ex.getMessage());
        }
    }

}
