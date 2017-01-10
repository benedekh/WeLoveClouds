package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;

/**
 * A start request to the {@link IMovableDataAccessService}, which starts the service.
 * 
 * @author Benedek
 */
public class StartDataAcessService implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(StartDataAcessService.class);

    private IMovableDataAccessService dataAccessService;

    public StartDataAcessService(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing start data access service request.");
            if (dataAccessService.getServiceStatus().equals(DataAccessServiceStatus.STOPPED)) {
                dataAccessService.setServiceStatus(DataAccessServiceStatus.STARTED);
                LOGGER.debug("Start data access service request finished susccessfully.");
                return createSuccessKVAdminMessage();
            } else {
                return createErrorKVAdminMessage(
                        "Data access service cannot be started, because it is not stopped.");
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
