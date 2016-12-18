package weloveclouds.server.services.replication.request;

import static weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType.PUT_ENTRY;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a PUT replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 */
public class PutReplicationRequest
        extends AbstractReplicationRequest<KVEntry, PutReplicationRequest.Builder> {

    protected PutReplicationRequest(Builder builder) {
        super(builder);
    }

    @Override
    public KVTransferMessage getTransferMessage() {
        return new KVTransferMessage.Builder().putableEntry(payload).status(PUT_ENTRY).build();
    }

    @Override
    public AbstractReplicationRequest<KVEntry, Builder> clone() {
        return new Builder().messageSerializer(messageSerializer).payload(payload).build();
    }

    @Override
    public String toString() {
        return StringUtils.join("", "PUT replication request with payload (", payload, ")");
    }

    /**
     * Builder pattern for creating a {@link PutReplicationRequest} instance.
     *
     * @author Benedek
     */
    public static class Builder extends AbstractReplicationRequest.Builder<KVEntry, Builder> {

        public PutReplicationRequest build() {
            return new PutReplicationRequest(this);
        }
    }

}
