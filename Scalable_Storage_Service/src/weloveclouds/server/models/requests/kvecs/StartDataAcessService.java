package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;

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
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
        } catch (UninitializedServiceException ex) {
            LOGGER.error(ex);
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage(ex.getMessage()).build();
        }
    }

}
