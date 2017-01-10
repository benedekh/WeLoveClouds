package weloveclouds.server.requests.kvserver.transfer;

import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createErrorKVTransferMessage;
import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createSuccessKVTransferMessage;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A transfer request to the {@link IMovableDataAccessService}, which means the storage units shall
 * be placed in the data access service.
 * 
 * @author Benedek
 */
public class Transfer implements IKVTransferRequest {

    private static final Logger LOGGER = Logger.getLogger(Transfer.class);

    private IMovableDataAccessService dataAccessService;
    private Set<MovableStorageUnit> storageUnits;

    /**
     * @param dataAccessService a reference to the data access service
     * @param storageUnits which shall be placed in the data access service
     */
    public Transfer(IMovableDataAccessService dataAccessService,
            Set<MovableStorageUnit> storageUnits) {
        this.dataAccessService = dataAccessService;
        this.storageUnits = storageUnits;
    }

    @Override
    public KVTransferMessage execute() {
        try {
            LOGGER.debug("Executing transfer (put) storage units request.");
            dataAccessService.putEntries(storageUnits);
            LOGGER.debug("Transfer (put) storage units request finished successfully.");
            return createSuccessKVTransferMessage();
        } catch (StorageException ex) {
            LOGGER.error(ex);
            return createErrorKVTransferMessage(ex.getMessage());
        }
    }

    @Override
    public IKVTransferRequest validate() throws IllegalArgumentException {
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
