package weloveclouds.server.services.datastore;

import java.util.Set;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.IReplicationService;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.MovablePersistentStorage;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.PutType;

/**
 * An implementation of {@link IReplicableDataAccessService} whose underlying storage units can be
 * replicated.
 * 
 * @author Benedek
 */
public class ReplicableDataAccessService extends MovableDataAccessService
        implements IReplicableDataAccessService {

    private volatile IReplicationService replicationService;

    public ReplicableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage,
            IReplicationService replicationService) {
        super(cache, persistentStorage);
        this.replicationService = replicationService;
    }

    @Override
    public void setReplicaConnectionInfos(Set<ServerConnectionInfo> replicaConnectionInfos) {
        replicationService.updateReplicaConnectionInfos(replicaConnectionInfos);
    }

    @Override
    protected PutType putEntry(KVEntry entry, boolean coordinatorRoleIsExpected)
            throws StorageException {
        PutType response = super.putEntry(entry, coordinatorRoleIsExpected);
        if (coordinatorRoleIsExpected) {
            replicationService.putEntryOnReplicas(entry);
        }
        return response;
    }

    @Override
    protected void removeEntry(String key, boolean coordinatorRoleIsExpected)
            throws StorageException {
        super.removeEntry(key, coordinatorRoleIsExpected);
        if (coordinatorRoleIsExpected) {
            replicationService.removeEntryOnReplicas(key);
        }
    }

}
