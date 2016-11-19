package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.models.DataAccessServiceStatus;

public class LockWriteAccess implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;

    public LockWriteAccess(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        if (dataAccessService == null) {
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage("Service is not initialized yet.").build();
        }
        
        dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_ACTIVE);
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

}
