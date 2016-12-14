package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.datastore.IMovableDataAccessService;

/**
 * A remove range request to the {@link IMovableDataAccessService}, which removes those entries
 * whose key's hash value is in the specified range.
 * 
 * @author Benedek
 */
public class RemoveRange implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(RemoveRange.class);

    private IMovableDataAccessService dataAccessService;
    private HashRange range;

    public RemoveRange(IMovableDataAccessService dataAccessService, HashRange range) {
        this.dataAccessService = dataAccessService;
        this.range = range;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing remove range request.");
            dataAccessService.removeEntries(range);
            dataAccessService.defragment();
            LOGGER.debug("Remove range request finished successfully.");
        } catch (Exception ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return createSuccessKVAdminMessage();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateHashRange(range);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Hash range is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

}
