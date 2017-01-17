package weloveclouds.server.requests.kvecs.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.transaction.ITransactionSenderService;
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

    private ITransactionSenderService transactionService;
    private ServerConnectionInfo connectionInfo;

    protected StorageUnitsTransporter(Builder builder) {
        this.connectionInfo = builder.connectionInfo;
        this.transactionService = builder.transactionService;
    }

    /**
     * Transfers the respective MovableStorageUnit instances to the target server. Creates bunches
     * from those units that will be transferred together.
     * 
     * @throws UnableToSendContentToServerException if an error occurs
     */
    public void transferStorageUnits(Set<MovableStorageUnit> storageUnitsToTransferred) {
        Set<MovableStorageUnit> toBeTransferred = new HashSet<>();
        for (MovableStorageUnit strageUnitToBeMoved : storageUnitsToTransferred) {
            LOGGER.debug(strageUnitToBeMoved);
            toBeTransferred.add(strageUnitToBeMoved);

            if (toBeTransferred.size() == NUMBER_OF_STORAGE_UNITS_TO_BE_TRANSFERRED_AT_ONCE) {
                transferBunchOverTheNetwork(toBeTransferred);
                toBeTransferred.clear();
            }
        }
        LOGGER.debug("SENKI");
        transferBunchOverTheNetwork(toBeTransferred);
    }

    /**
     * Transfers a bunch of storage units over the network to the target server.
     */
    private void transferBunchOverTheNetwork(Set<MovableStorageUnit> storageUnits) {
        LOGGER.info(StringUtils.join("", "Transfering bunch (", storageUnits.size(),
                ") of storage units over the network."));
        KVTransferMessage transferMessage = new KVTransferMessage.Builder()
                .status(StatusType.TRANSFER_ENTRIES).storageUnits(storageUnits).build();
        transactionService.executeTransactionsFor(transferMessage,
                new HashSet<>(Arrays.asList(connectionInfo)));
    }

    /**
     * Builder pattern for creating a {@link StorageUnitsTransporter} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ServerConnectionInfo connectionInfo;
        private ITransactionSenderService transactionService;

        /**
         * @param connectionInfo the IP + port of the destination
         */
        public Builder connectionInfo(ServerConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder transactionService(ITransactionSenderService transactionService) {
            this.transactionService = transactionService;
            return this;
        }

        public StorageUnitsTransporter build() {
            return new StorageUnitsTransporter(this);
        }
    }

}
