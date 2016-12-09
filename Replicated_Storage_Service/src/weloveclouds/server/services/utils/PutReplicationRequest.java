package weloveclouds.server.services.utils;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;

/**
 * Represents a PUT replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 */
public class PutReplicationRequest
        extends AbstractReplicationRequest<KVEntry, PutReplicationRequest.Builder> {

    public PutReplicationRequest(Builder builder) {
        super(builder);
    }

    @Override
    protected KVTransferMessage createTransferMessageFrom(KVEntry payload) {
        return new KVTransferMessage.Builder().putableEntry(payload).status(StatusType.PUT_ENTRY)
                .build();
    }

    /**
     * Builder pattern for creating a {@link PutReplicationRequest} instance.
     *
     * @author Benedek
     */
    public static class Builder extends AbstractReplicationRequest.Builder<KVEntry, Builder> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public PutReplicationRequest build() {
            super.logger(Logger.getLogger(PutReplicationRequest.class));
            return new PutReplicationRequest(this);
        }
        
    }

}
