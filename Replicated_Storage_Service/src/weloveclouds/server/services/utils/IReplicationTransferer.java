package weloveclouds.server.services.utils;

import weloveclouds.kvstore.models.KVEntry;


/**
 * Common methods for those classes which can replicate the requests on replicas.
 * 
 * @author Benedek
 */
public interface IReplicationTransferer {

    /**
     * Puts the respective entry on the replica nodes.
     */
    void putEntryOnReplicas(KVEntry entry);

    /**
     * Removes the entry denoted by the respective key on the replica nodes.
     */
    void removeEntryOnReplicas(String key);

}
