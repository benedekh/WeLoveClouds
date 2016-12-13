package weloveclouds.server.services;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.services.utils.IReplicationTransferer;
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

    private volatile IReplicationTransferer replicationTransferer;

    public ReplicableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage) {
        super(cache, persistentStorage);
    }

    @Override
    public synchronized void setReplicationTransferer(
            IReplicationTransferer replicationTransferer) {
        this.replicationTransferer = replicationTransferer;
    }

    @Override
    protected PutType putEntry(KVEntry entry, boolean coordinatorRoleIsExpected)
            throws StorageException {
        PutType response = super.putEntry(entry, coordinatorRoleIsExpected);
        if (coordinatorRoleIsExpected && replicationTransferer != null) {
            replicationTransferer.putEntryOnReplicas(entry);
        }
        return response;
    }

    @Override
    protected void removeEntry(String key, boolean coordinatorRoleIsExpected)
            throws StorageException {
        super.removeEntry(key, coordinatorRoleIsExpected);
        if (coordinatorRoleIsExpected && replicationTransferer != null) {
            replicationTransferer.removeEntryOnReplicas(key);
        }
    }

}
