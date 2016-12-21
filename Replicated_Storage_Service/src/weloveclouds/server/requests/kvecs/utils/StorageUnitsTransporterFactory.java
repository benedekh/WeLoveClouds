package weloveclouds.server.requests.kvecs.utils;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

/**
 * A factory to create a {@link StorageUnitsTransporter} instance.
 * 
 * @author Benedek
 */
public class StorageUnitsTransporterFactory {

    /**
     * A factory method to create a {@link StorageUnitsTransporter} instance based on its arguments.
     * 
     * @param connectionInfo the IP + port of the destination
     */
    public StorageUnitsTransporter createStorageUnitsTransporter(
            ServerConnectionInfo connectionInfo) {
        return new StorageUnitsTransporter.Builder().connectionInfo(connectionInfo)
                .transactionService(
                        new TransactionServiceFactory().create2PCTransactionSenderService())
                .build();
    }
}
