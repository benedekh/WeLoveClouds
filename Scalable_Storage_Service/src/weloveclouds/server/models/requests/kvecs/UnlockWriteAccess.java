package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.models.DataAccessServiceStatus;

public class UnlockWriteAccess implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;

    public UnlockWriteAccess(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        if (dataAccessService == null) {
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage("Service is not initialized yet.").build();
        }

        dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_INACTIVE);
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }
}
