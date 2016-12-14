package weloveclouds.server.services.replication.request;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
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
    protected KVTransferMessage createTransferMessageFrom(KVEntry payload) {
        return new KVTransferMessage.Builder().putableEntry(payload).status(StatusType.PUT_ENTRY)
                .build();
    }

    @Override
    public AbstractReplicationRequest<KVEntry, Builder> clone() {
        return new Builder().communicationApi(super.communicationApi).logger(super.logger)
                .messageDeserializer(super.messageDeserializer)
                .messageSerializer(super.messageSerializer).payload(super.payload).build();
    }

    @Override
    public String toString() {
        return StringUtils.join("", "PUT replication request with payload (", super.payload, ")");
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
