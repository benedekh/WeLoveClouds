package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;

public class LockWriteAccess implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;

    public LockWriteAccess(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_ACTIVE);
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
        } catch (UninitializedServiceException ex) {
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage(ex.getMessage()).build();
        }
    }

}
