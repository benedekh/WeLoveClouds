package weloveclouds.server.services.utils;

import weloveclouds.kvstore.models.KVEntry;


public interface IReplicationTransferer {

    void putEntryOnReplicas(KVEntry entry);

    void removeKeyOnReplicas(String key); 

}
