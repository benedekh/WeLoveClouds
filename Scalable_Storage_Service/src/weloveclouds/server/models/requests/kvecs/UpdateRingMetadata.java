package weloveclouds.server.models.requests.kvecs;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * An update metadata request to the {@link IMovableDataAccessService}, which defines in what range
 * shall be the keys of the entries which are stored on this server.
 *
 * @author Benedek
 */
public class UpdateRingMetadata implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;

    private RingMetadata ringMetadata;
    private HashRange rangeManagedByServer;

    /**
     * @param dataAccessService a reference to the data access service
     * @param ringMetadata the metadata information about the ring in which the servers are placed
     * @param rangeManagedByServer the range of hashes which are managed by the server
     */
    public UpdateRingMetadata(IMovableDataAccessService dataAccessService,
            RingMetadata ringMetadata, HashRange rangeManagedByServer) {
        this.ringMetadata = ringMetadata;
        this.rangeManagedByServer = rangeManagedByServer;
    }

    @Override
    public KVAdminMessage execute() {
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRange(rangeManagedByServer);
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

}
