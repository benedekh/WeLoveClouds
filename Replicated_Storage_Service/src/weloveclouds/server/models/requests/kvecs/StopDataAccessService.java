package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;

/**
 * A start request to the {@link IMovableDataAccessService}, which stops the service.
 * 
 * @author Benedek
 */
public class StopDataAccessService implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(StopDataAccessService.class);

    private IMovableDataAccessService dataAccessService;

    public StopDataAccessService(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing stop data access service request.");
            dataAccessService.setServiceStatus(DataAccessServiceStatus.STOPPED);
            LOGGER.debug("Stop data access service request finished susccessfully.");
            return createSuccessKVAdminMessage();
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
