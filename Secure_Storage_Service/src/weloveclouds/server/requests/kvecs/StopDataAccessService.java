package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;

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
            if (!dataAccessService.getServiceStatus().equals(DataAccessServiceStatus.STOPPED)) {
                dataAccessService.setServiceStatus(DataAccessServiceStatus.STOPPED);
                LOGGER.debug("Stop data access service request finished susccessfully.");
                return createSuccessKVAdminMessage();
            } else {
                return createErrorKVAdminMessage("Data access service is already stopped.");
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
