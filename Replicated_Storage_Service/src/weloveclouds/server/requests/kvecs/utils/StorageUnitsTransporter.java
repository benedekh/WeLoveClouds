package weloveclouds.server.requests.kvecs.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.store.models.MovableStorageUnit;
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

    protected StorageUnitsTransporter(Builder builder) {
        this.communicationApi = builder.communicationApi;
        this.connectionInfo = builder.connectionInfo;
        this.transferMessageSerializer = builder.transferMessageSerializer;
        this.transferMessageDeserializer = builder.transferMessageDeserializer;
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
                transferBunchOverTheNetwork(toBeTransferred);
                toBeTransferred.clear();
            }
        }
        transferBunchOverTheNetwork(toBeTransferred);
    }

    /**
     * Transfers a bunch of storage units over the network to the target server.
     * 
     * @throws UnableToSendContentToServerException if an error occurs
     */
    private void transferBunchOverTheNetwork(Set<MovableStorageUnit> storageUnits)
            throws UnableToSendContentToServerException {
        try {
            KVTransferMessage transferMessage = new KVTransferMessage.Builder()
                    .status(StatusType.TRANSFER_ENTRIES).storageUnits(storageUnits).build();
            SerializedMessage serializedMessage =
                    transferMessageSerializer.serialize(transferMessage);

            byte[] responsePacket =
                    communicationApi.sendAndExpectForResponse(serializedMessage.getBytes());

            KVTransferMessage response = transferMessageDeserializer.deserialize(responsePacket);
            if (response.getStatus() == StatusType.RESPONSE_ERROR) {
                throw new UnableToSendContentToServerException(response.getResponseMessage());
            }
        } catch (DeserializationException | IOException ex) {
            throw new UnableToSendContentToServerException(ex.getMessage());
        }
    }

    /**
     * Builder pattern for creating a {@link StorageUnitsTransporter} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ICommunicationApi communicationApi;
        private ServerConnectionInfo connectionInfo;
        private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
        private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

        /**
         * @param connectionInfo the IP + port of the destination
         */
        public Builder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
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

        public StorageUnitsTransporter build() {
            return new StorageUnitsTransporter(this);
        }
    }

}
