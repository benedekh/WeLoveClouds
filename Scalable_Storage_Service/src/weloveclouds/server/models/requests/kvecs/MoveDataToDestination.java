package weloveclouds.server.models.requests.kvecs;

import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;

public class MoveDataToDestination implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;
    private RingMetadataPart targetServerInfo;
    
    public MoveDataToDestination(IMovableDataAccessService dataAccessService,
            RingMetadataPart serverInfo) {
        this.dataAccessService = dataAccessService;
        this.targetServerInfo = targetServerInfo;
    }

    @Override
    public KVAdminMessage execute() {
        
        
        
        // TODO Auto-generated method stub
        return null;
    }

}
