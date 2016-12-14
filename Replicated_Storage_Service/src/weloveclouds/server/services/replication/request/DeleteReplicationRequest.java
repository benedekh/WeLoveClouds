package weloveclouds.server.services.replication.request;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
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
    protected KVTransferMessage createTransferMessageFrom(String payload) {
        return new KVTransferMessage.Builder().removableKey(payload)
                .status(StatusType.REMOVE_ENTRY_BY_KEY).build();
    }

    @Override
    public AbstractReplicationRequest<String, Builder> clone() {
        return new Builder().communicationApi(super.communicationApi).logger(super.logger)
                .messageDeserializer(super.messageDeserializer)
                .messageSerializer(super.messageSerializer).payload(super.payload).build();
    }

    @Override
    public String toString() {
        return StringUtils.join("", "DELETE replication request with payload (", super.payload,
                ")");
    }

    /**
     * Builder pattern for creating a {@link DeleteReplicationRequest} instance.
     *
     * @author Benedek
     */
    public static class Builder extends AbstractReplicationRequest.Builder<String, Builder> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public DeleteReplicationRequest build() {
            super.logger(Logger.getLogger(DeleteReplicationRequest.class));
            return new DeleteReplicationRequest(this);
        }
    }

}
