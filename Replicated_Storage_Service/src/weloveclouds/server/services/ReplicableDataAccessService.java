package weloveclouds.server.services;

import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.services.utils.IReplicationTransferer;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.MovablePersistentStorage;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;

public class ReplicableDataAccessService extends MovableDataAccessService {

    private IReplicationTransferer replicationTransferer;

    public ReplicableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage,
            IReplicationTransferer replicationTransferer) {
        super(cache, persistentStorage);
        this.replicationTransferer = replicationTransferer;
    }

    @Override
    protected PutType putEntry(KVEntry entry, boolean coordinatorRoleIsExpected)
            throws StorageException {
        PutType response = super.putEntry(entry, coordinatorRoleIsExpected);
        if (coordinatorRoleIsExpected) {
            replicationTransferer.putEntryOnReplicas(entry);
        }
        return response;
    }

    @Override
    protected void removeEntry(String key, boolean coordinatorRoleIsExpected)
            throws StorageException {
        super.removeEntry(key, coordinatorRoleIsExpected);
        if (coordinatorRoleIsExpected) {
            replicationTransferer.removeKeyOnReplicas(key);
        }
    }

}
