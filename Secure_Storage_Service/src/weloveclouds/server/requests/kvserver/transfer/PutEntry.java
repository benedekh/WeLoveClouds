package weloveclouds.server.requests.kvserver.transfer;

import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createErrorKVTransferMessage;
import static weloveclouds.server.requests.kvserver.transfer.utils.KVTransferMessageFactory.createSuccessKVTransferMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A transfer request to the {@link IMovableDataAccessService}, which means the respective entry
 * shall be put into the data access service.
 * 
 * @author Benedek
 */
public class PutEntry implements IKVTransferRequest {

    private static final Logger LOGGER = Logger.getLogger(PutEntry.class);

    private IMovableDataAccessService dataAccessService;
    private KVEntry entry;

    /**
     * @param dataAccessService a reference to the data access service
     * @param entry that shall be put into the data access service
     */
    public PutEntry(IMovableDataAccessService dataAccessService, KVEntry entry) {
        this.dataAccessService = dataAccessService;
        this.entry = entry;
    }

    @Override
    public KVTransferMessage execute() {
        try {
            LOGGER.debug("Executing put entry request.");
            dataAccessService.putEntryWithoutAuthorization(entry);
            LOGGER.debug("Put entry request finished successfully.");
            return createSuccessKVTransferMessage();
        } catch (StorageException ex) {
            LOGGER.error(ex);
            return createErrorKVTransferMessage(ex.getMessage());
        }
    }

    @Override
    public IKVTransferRequest validate() throws IllegalArgumentException {
        if (entry == null) {
            String errorMessage = "Entry cannot be null.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVTransferMessage(errorMessage));
        }
        return this;
    }

}
