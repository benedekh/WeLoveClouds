package weloveclouds.server.services.replication;

import weloveclouds.commons.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.server.services.replication.request.StatefulReplicationFactory;

public class ReplicationServiceFactory {

    public ReplicationService createReplicationService() {
        return new ReplicationService.Builder()
                .replicationExecutorFactory(new ReplicationExecutorFactory())
                .statefulReplicationFactory(new StatefulReplicationFactory.Builder()
                        .messageDeserializer(new KVTransferMessageDeserializer())
                        .messageSerializer(new KVTransferMessageSerializer())
                        .communicationApi(
                                new CommunicationApiFactory().createConcurrentCommunicationApiV1())
                        .build())
                .build();

    }

}
