package weloveclouds.server.models.requests.kvecs;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;

public class UpdateRingMetadata implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;

    private RingMetadata ringMetadata;
    private HashRange rangeManagedByServer;

    public UpdateRingMetadata(IMovableDataAccessService dataAccessService,
            RingMetadata ringMetadata, HashRange rangeManagedByServer) {
        this.ringMetadata = ringMetadata;
        this.rangeManagedByServer = rangeManagedByServer;
    }

    @Override
    public KVAdminMessage execute() {
        if (dataAccessService == null) {
            return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                    .responseMessage("Service is not initialized yet.").build();
        }

        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRange(rangeManagedByServer);
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

}
