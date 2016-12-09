package weloveclouds.server.services;

import weloveclouds.server.services.utils.IReplicationTransferer;

/**
 * A common interface to those {@link IMovableDataAccessService} implementations whose underlying
 * storage units can be replicated.
 * 
 * @author Benedek
 */
public interface IReplicableDataAccessService extends IMovableDataAccessService {

    /**
     * Sets the replication transferer.
     */
    void setReplicationTransferer(IReplicationTransferer replicationTransferer);

}
