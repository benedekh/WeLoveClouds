package weloveclouds.ecs.models.commands.internal;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.SYNCHRONIZED;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.INITKVSERVER;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

/**
 * Created by Benoit on 2016-11-22.
 */
public class InitNodeMetadata extends AbstractEcsNetworkCommand {
    private RingMetadata ringMetadata;

    protected InitNodeMetadata(Builder initNodeMetadataBuilder) {
        this.communicationApi = initNodeMetadataBuilder.communicationApi;
        this.ringMetadata = initNodeMetadataBuilder.ringMetadata;
        this.targetedNode = initNodeMetadataBuilder.targetedNode;
        this.messageSerializer = initNodeMetadataBuilder.messageSerializer;
        this.messageDeserializer = initNodeMetadataBuilder.messageDeserializer;
        this.errorMessage = CustomStringJoiner.join(" ", "Unable to initialize metadata on node:",
                targetedNode.toString());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message = new KVAdminMessage.Builder()
                    .status(INITKVSERVER)
                    .ringMetadata(ringMetadata)
                    .targetServerInfo(ringMetadata.findServerInfoByHash(targetedNode.getHashKey()))
                    .build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            KVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            } else {
                targetedNode.setStatus(INITIALIZED);
                targetedNode.setMetadataStatus(SYNCHRONIZED);
            }

        } catch (ClientSideException | DeserializationException ex) {
            throw new ClientSideException(errorMessage, ex);
        } finally {
            try {
                communicationApi.disconnect();
            } catch (UnableToDisconnectException ex) {
                //LOG
            }
        }
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "Command: InitNodeMetadata", "Targeted node:",
                targetedNode.toString());
    }

    public static class Builder {
        private ICommunicationApi communicationApi;
        private RingMetadata ringMetadata;
        private StorageNode targetedNode;
        private IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer;
        private IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer;

        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        public Builder targetedNode(StorageNode targetedNode) {
            this.targetedNode = targetedNode;
            return this;
        }

        public Builder messageSerializer(IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public Builder messageDeserializer(IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public InitNodeMetadata build() {
            return new InitNodeMetadata(this);
        }
    }
}
