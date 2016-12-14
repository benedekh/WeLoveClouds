package weloveclouds.server.services.replication.request;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.IConcurrentCommunicationApi;

public class StatefulReplicationFactory {

    private IConcurrentCommunicationApi concurrentCommunicationApi;
    private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer;

    public StatefulReplicationFactory(Builder builder) {
        this.concurrentCommunicationApi = builder.concurrentCommunicationApi;
        this.transferMessageSerializer = builder.transferMessageSerializer;
        this.transferMessageDeserializer = builder.transferMessageDeserializer;
    }

    public AbstractReplicationRequest<?, ?> createPutReplicationRequest(KVEntry entry) {
        return new PutReplicationRequest.Builder().communicationApi(concurrentCommunicationApi)
                .payload(entry).messageSerializer(transferMessageSerializer)
                .messageDeserializer(transferMessageDeserializer).build();
    }

    public AbstractReplicationRequest<?, ?> createDeleteReplicationRequest(String key) {
        return new DeleteReplicationRequest.Builder().communicationApi(concurrentCommunicationApi)
                .payload(key).messageSerializer(transferMessageSerializer)
                .messageDeserializer(transferMessageDeserializer).build();
    }

    /**
     * Builder pattern for creating a {@link StatefulReplicationFactory} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IConcurrentCommunicationApi concurrentCommunicationApi;
        private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;
        private IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer;

        public Builder communicationApi(IConcurrentCommunicationApi concurrentCommunicationApi) {
            this.concurrentCommunicationApi = concurrentCommunicationApi;
            return this;
        }

        public Builder messageSerializer(
                IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer) {
            this.transferMessageSerializer = transferMessageSerializer;
            return this;
        }

        public Builder messageDeserializer(
                IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer) {
            this.transferMessageDeserializer = transferMessageDeserializer;
            return this;
        }

        public StatefulReplicationFactory build() {
            return new StatefulReplicationFactory(this);
        }
    }

}
