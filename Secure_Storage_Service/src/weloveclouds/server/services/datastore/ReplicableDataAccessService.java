package weloveclouds.server.services.datastore;

import java.util.Set;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.IReplicationService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.PutType;

/**
 * An implementation of {@link IReplicableDataAccessService} whose underlying storage units can be
 * replicated.
 * 
 * @author Benedek
 */
public class ReplicableDataAccessService
        extends MovableDataAccessService<ReplicableDataAccessService.Builder>
        implements IReplicableDataAccessService {

    private volatile IReplicationService replicationService;

    protected ReplicableDataAccessService(Builder builder) {
        super(builder);
        this.replicationService = builder.replicationService;
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

    public static class Builder extends MovableDataAccessService.Builder<Builder> {
        private IReplicationService replicationService;

        public Builder replicationService(IReplicationService replicationService) {
            this.replicationService = replicationService;
            return this;
        }

        public ReplicableDataAccessService build() {
            return new ReplicableDataAccessService(this);
        }

    }
}
