package weloveclouds.server.models.requests.kvserver;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.networking.models.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A transfer request to the {@link IMovableDataAccessService}, which means the storage units shall
 * be placed in the data access service.
 * 
 * @author Benedek
 */
public class Transfer implements IKVServerRequest {

    private static final Logger LOGGER = Logger.getLogger(Transfer.class);

    private IMovableDataAccessService dataAccessService;
    private MovableStorageUnits storageUnits;

    /**
     * @param dataAccessService a reference to the data access service
     * @param storageUnits which shall be placed in the data access service
     */
    public Transfer(IMovableDataAccessService dataAccessService, MovableStorageUnits storageUnits) {
        this.dataAccessService = dataAccessService;
        this.storageUnits = storageUnits;
    }

    @Override
    public KVTransferMessage execute() {
        try {
            LOGGER.debug("Executing transfer (put) storage units request.");
            dataAccessService.putEntries(storageUnits);
            LOGGER.debug("Transfer (put) storage units request finished successfully.");
            return new KVTransferMessage.Builder().status(StatusType.TRANSFER_SUCCESS).build();
        } catch (StorageException ex) {
            LOGGER.error(ex);
            return createErrorKVTransferMessage(ex.getMessage());
        }
    }

    private KVTransferMessage createErrorKVTransferMessage(String responseMessage) {
        return new KVTransferMessage.Builder().status(StatusType.TRANSFER_ERROR)
                .responseMessage(responseMessage).build();
    }

    @Override
    public IKVServerRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateMovableStorageUnits(storageUnits);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Storage units that shall be copied are invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVTransferMessage(errorMessage));
        }
        return this;
    }

}
