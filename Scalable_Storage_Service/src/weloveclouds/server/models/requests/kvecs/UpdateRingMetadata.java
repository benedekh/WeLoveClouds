package weloveclouds.server.models.requests.kvecs;

import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;

public class UpdateRingMetadata implements IKVECSRequest {

    private RingMetadata previousRingMetadata;
    private RingMetadataPart previousRangeManagedByServer;

    private RingMetadata newRingMetadata;
    private RingMetadataPart newRangeManagedByServer;

    public UpdateRingMetadata(RingMetadata previousRingMetadata,
            RingMetadataPart previousRangeManagedByServer, RingMetadata newRingMetadata,
            RingMetadataPart newRangeManagedByServer) {
        this.previousRangeManagedByServer = previousRangeManagedByServer;
        this.previousRingMetadata = previousRingMetadata;
        this.newRingMetadata = newRingMetadata;
        this.newRangeManagedByServer = newRangeManagedByServer;
    }

    @Override
    public KVAdminMessage execute() {
        this.previousRingMetadata = newRingMetadata;
        this.previousRangeManagedByServer = newRangeManagedByServer;
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

}
