package weloveclouds.server.services.replication.request;

import static weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType.REMOVE_ENTRY_BY_KEY;

import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a DELETE replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 */
public class DeleteReplicationRequest
        extends AbstractReplicationRequest<String, DeleteReplicationRequest.Builder> {

    protected DeleteReplicationRequest(Builder builder) {
        super(builder);
    }

    @Override
    public KVTransferMessage getTransferMessage() {
        return new KVTransferMessage.Builder().removableKey(payload).status(REMOVE_ENTRY_BY_KEY)
                .build();
    }

    @Override
    public AbstractReplicationRequest<String, Builder> clone() {
        return new Builder().messageSerializer(messageSerializer).payload(payload).build();
    }

    @Override
    public String toString() {
        return StringUtils.join("", "DELETE replication request with payload (", payload, ")");
    }

    /**
     * Builder pattern for creating a {@link DeleteReplicationRequest} instance.
     *
     * @author Benedek
     */
    public static class Builder extends AbstractReplicationRequest.Builder<String, Builder> {

        public DeleteReplicationRequest build() {
            return new DeleteReplicationRequest(this);
        }
    }

}
