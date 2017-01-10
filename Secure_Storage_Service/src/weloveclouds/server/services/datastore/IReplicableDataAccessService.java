package weloveclouds.server.services.datastore;

import java.util.Set;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A common interface to those {@link IMovableDataAccessService} implementations whose underlying
 * storage units can be replicated.
 * 
 * @author Benedek
 */
public interface IReplicableDataAccessService extends IMovableDataAccessService {

    public void setReplicaConnectionInfos(Set<ServerConnectionInfo> replicaConnectionInfos);

}
