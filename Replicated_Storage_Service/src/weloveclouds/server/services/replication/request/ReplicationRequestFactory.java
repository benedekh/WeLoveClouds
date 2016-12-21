package weloveclouds.server.services.replication.request;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;

public class ReplicationRequestFactory {

    private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;

    public ReplicationRequestFactory(
            IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer) {
        this.transferMessageSerializer = transferMessageSerializer;
    }

    public AbstractReplicationRequest<?, ?> createPutReplicationRequest(KVEntry entry) {
        return new PutReplicationRequest.Builder().payload(entry)
                .messageSerializer(transferMessageSerializer).build();
    }

    public AbstractReplicationRequest<?, ?> createDeleteReplicationRequest(String key) {
        return new DeleteReplicationRequest.Builder().payload(key)
                .messageSerializer(transferMessageSerializer).build();
    }

}
