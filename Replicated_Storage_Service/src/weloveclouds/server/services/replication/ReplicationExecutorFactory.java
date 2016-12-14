package weloveclouds.server.services.replication;

import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.models.ConnectionFactory;

public class ReplicationExecutorFactory {

    public ReplicationExecutor createReplicationExecutor() {
        return new ReplicationExecutor(new ConnectionFactory(new SocketFactory()));
    }

}
