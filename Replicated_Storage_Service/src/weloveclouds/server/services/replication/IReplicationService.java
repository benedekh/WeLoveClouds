package weloveclouds.server.services.replication;

import java.util.Set;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.communication.models.ServerConnectionInfo;


/**
 * Common methods for those classes which can replicate the requests on replicas.
 * 
 * @author Benedek
 */
public interface IReplicationService {

    /**
     * Puts the respective entry on the replica nodes.
     */
    public void putEntryOnReplicas(KVEntry entry);

    /**
     * Removes the entry denoted by the respective key on the replica nodes.
     */
    public void removeEntryOnReplicas(String key);

    public void updateReplicaConnectionInfos(Set<ServerConnectionInfo> replicationConnectionInfos);
    
    public void start();
    
    public void stop();
    
    public boolean isRunning();

}
