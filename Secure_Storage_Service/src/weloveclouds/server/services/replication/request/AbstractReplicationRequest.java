package weloveclouds.server.services.replication.request;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;

/**
 * An abstract class which represents a replication request that shall be executed on the replicas.
 * 
 * @author Benedek
 *
 * @param <T> The type of the payload that is transferred in the {@link ITransferMessage}.
 */
public abstract class AbstractReplicationRequest<T, E extends AbstractReplicationRequest.Builder<T, E>> {

    protected T payload;
    protected IMessageSerializer<SerializedMessage, IKVTransferMessage> messageSerializer;

    protected AbstractReplicationRequest(Builder<T, E> builder) {
        this.payload = builder.payload;
        this.messageSerializer = builder.messageSerializer;
    }

    /**
     * @return the transfer message which will be forwarded to the replicas
     */
    public abstract KVTransferMessage getTransferMessage();

    /**
     * Creates a deep copy of the object.
     */
    public abstract AbstractReplicationRequest<T, E> clone();

    /**
     * Builder pattern for creating a {@link AbstractReplicationRequest} instance.
     *
     * @author Benedek
     */
    public abstract static class Builder<T, E extends Builder<T, E>> {
        protected T payload;
        protected IMessageSerializer<SerializedMessage, IKVTransferMessage> messageSerializer;

        @SuppressWarnings("unchecked")
        public E payload(T payload) {
            this.payload = payload;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E messageSerializer(
                IMessageSerializer<SerializedMessage, IKVTransferMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return (E) this;
        }
    }
}
