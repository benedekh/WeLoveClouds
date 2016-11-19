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
        dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_INACTIVE);
        return new KVAdminMessage.KVAdminMessageBuilder().status(StatusType.RESPONSE_SUCCESS)
                .build();
    }
}
