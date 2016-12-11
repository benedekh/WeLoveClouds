package weloveclouds.server.models.requests.kvecs;

import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
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
