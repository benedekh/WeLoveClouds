package weloveclouds.server.requests.kvserver.transfer;

import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createErrorKVTransferMessage;
import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createSuccessKVTransferMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A transfer request to the {@link IMovableDataAccessService}, which means entry denoted by its key
 * has to be removed from the data access service.
 * 
 * @author Benedek
 */
public class RemoveEntry implements IKVTransferRequest {

    private static final Logger LOGGER = Logger.getLogger(RemoveEntry.class);

    private IMovableDataAccessService dataAccessService;
    private String key;

    /**
     * @param dataAccessService a reference to the data access service
     * @param key that shall be removed from the data access service
     */
    public RemoveEntry(IMovableDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
    }

    @Override
    public KVTransferMessage execute() {
        try {
            LOGGER.debug("Executing remove entry request.");
            dataAccessService.removeEntryWithoutAuthorization(key);
            LOGGER.debug("Remove entry request finished successfully.");
            return createSuccessKVTransferMessage();
        } catch (StorageException ex) {
            LOGGER.error(ex);
            return createErrorKVTransferMessage(ex.getMessage());
        }
    }

    @Override
    public IKVTransferRequest validate() throws IllegalArgumentException {
        if (key == null) {
            String errorMessage = "Key cannot be null.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVTransferMessage(errorMessage));
        }
        return this;
    }

}
