package weloveclouds.server.services.utils;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;

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
