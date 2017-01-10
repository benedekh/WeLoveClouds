package weloveclouds.server.services.replication.request;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;

/**
 * Factory to create replication requests.
 * 
 * @author Benedek
 */
public class ReplicationRequestFactory {

    private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;

    public ReplicationRequestFactory(
            IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer) {
        this.transferMessageSerializer = transferMessageSerializer;
    }

    /**
     * @return a {@link PutReplicationRequest} for the respective entry.
     */
    public PutReplicationRequest createPutReplicationRequest(KVEntry entry) {
        return new PutReplicationRequest.Builder().payload(entry)
                .messageSerializer(transferMessageSerializer).build();
    }

    /**
     * @return a {@link DeleteReplicationRequest} for the respective key.
     */
    public DeleteReplicationRequest createDeleteReplicationRequest(String key) {
        return new DeleteReplicationRequest.Builder().payload(key)
                .messageSerializer(transferMessageSerializer).build();
    }

}
