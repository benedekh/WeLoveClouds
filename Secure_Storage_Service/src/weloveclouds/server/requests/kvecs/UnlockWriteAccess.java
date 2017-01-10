package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;

/**
 * An unlock request to the {@link IMovableDataAccessService}, which releases the previously
 * activate write lock.
 * 
 * @author Benedek
 */
public class UnlockWriteAccess implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(UnlockWriteAccess.class);

    private IMovableDataAccessService dataAccessService;

    public UnlockWriteAccess(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing unlock write request.");
            if (dataAccessService.getServiceStatus()
                    .equals(DataAccessServiceStatus.WRITELOCK_ACTIVE)) {
                dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_INACTIVE);
                LOGGER.debug("Unlock write request finished successfully.");
                return createSuccessKVAdminMessage();
            } else {
                return createErrorKVAdminMessage(
                        "Write lock was not active on the data access service.");
            }
        } catch (UninitializedServiceException ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        return this;
    }
}
