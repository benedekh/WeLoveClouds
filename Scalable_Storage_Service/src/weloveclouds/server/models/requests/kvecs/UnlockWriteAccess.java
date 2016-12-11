package weloveclouds.server.models.requests.kvecs;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;

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
            dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_INACTIVE);
            LOGGER.debug("Unlock write request finished successfully.");
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
        } catch (UninitializedServiceException ex) {
            LOGGER.error(ex);
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage(ex.getMessage()).build();
        }
    }
    
    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        return this;
    }
}
