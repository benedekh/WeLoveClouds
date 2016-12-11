package weloveclouds.server.services.utils;

import java.util.Set;

import weloveclouds.commons.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.communication.services.NetworkPacketResenderFactory;

/**
 * A factory to create {@link ReplicationTransferer} instances.
 * 
 * @author Benedek
 */
public class ReplicationTransfererFactory {

    /**
     * @return a {@link IReplicationTransferer} instance based on the replica connection information
     */
    public IReplicationTransferer createReplicationTransferer(
            Set<ServerConnectionInfo> replicaConnectionInfos) {
        return new ReplicationTransferer.Builder()
                .communicationApi(new ConcurrentCommunicationApiV1(
                        new ConcurrentCommunicationService(), new NetworkPacketResenderFactory()))
                .connectionFactory(new ConnectionFactory(new SocketFactory()))
                .replicaConnectionInfos(replicaConnectionInfos)
                .messageSerializer(new KVTransferMessageSerializer())
                .messageDeserializer(new KVTransferMessageDeserializer()).build();
    }

}
