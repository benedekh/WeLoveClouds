package weloveclouds.server.requests.kvserver;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A transfer request to the {@link IMovableDataAccessService}, which means the storage units shall
 * be placed in the data access service.
 * 
 * @author Benedek
 */
public class RemoveEntry implements IKVServerRequest {

    private static final Logger LOGGER = Logger.getLogger(Transfer.class);

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
            return new KVTransferMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
        } catch (StorageException ex) {
            LOGGER.error(ex);
            return createErrorKVTransferMessage(ex.getMessage());
        }
    }

    private KVTransferMessage createErrorKVTransferMessage(String responseMessage) {
        return new KVTransferMessage.Builder().status(StatusType.RESPONSE_ERROR)
                .responseMessage(responseMessage).build();
    }

    @Override
    public IKVServerRequest validate() throws IllegalArgumentException {
        if (key == null) {
            String errorMessage = "Key cannot be null.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVTransferMessage(errorMessage));
        }
        return this;
    }

}
