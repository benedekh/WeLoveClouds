package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.models.DataAccessServiceStatus;

public class StartDataAcessService implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;

    public StartDataAcessService(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        dataAccessService.setServiceStatus(DataAccessServiceStatus.STARTED);
        return new KVAdminMessage.KVAdminMessageBuilder().status(StatusType.RESPONSE_SUCCESS)
                .build();
    }

}
