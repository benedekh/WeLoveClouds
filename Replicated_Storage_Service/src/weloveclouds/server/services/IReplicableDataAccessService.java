package weloveclouds.server.services;

import weloveclouds.server.services.utils.IReplicationTransferer;


public interface IReplicableDataAccessService extends IMovableDataAccessService {

    void setReplicationTransferer(IReplicationTransferer replicationTransferer);

}
