package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;

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
            dataAccessService.setServiceStatus(DataAccessServiceStatus.STARTED);
            LOGGER.debug("Start data access service request finished susccessfully.");
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
