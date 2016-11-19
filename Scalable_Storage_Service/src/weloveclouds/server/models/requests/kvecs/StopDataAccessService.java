package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
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

    private IMovableDataAccessService dataAccessService;

    public StopDataAccessService(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            dataAccessService.setServiceStatus(DataAccessServiceStatus.STOPPED);
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
        } catch (UninitializedServiceException ex) {
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage(ex.getMessage()).build();
        }
    }

}
