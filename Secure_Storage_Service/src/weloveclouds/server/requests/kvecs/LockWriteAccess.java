package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;

/**
 * A lock request to the {@link IMovableDataAccessService}, which forbids PUT and DELETE requests to
 * be processed.
 * 
 * @author Benedek
 */
public class LockWriteAccess implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(LockWriteAccess.class);

    private IMovableDataAccessService dataAccessService;

    public LockWriteAccess(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing lock write request.");
            if (dataAccessService.getServiceStatus().equals(DataAccessServiceStatus.STARTED)) {
                dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_ACTIVE);
                LOGGER.debug("Lock write request finished successfully.");
                return createSuccessKVAdminMessage();
            } else {
                return createErrorKVAdminMessage(
                        "Data access service is not started yet or write lock is already active.");
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
