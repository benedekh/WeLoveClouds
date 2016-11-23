package weloveclouds.ecs.models.commands.internal;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

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
            communicationApi.connectTo(targetedNode.getServerConnectionInfo());
            KVAdminMessage message = new KVAdminMessage.Builder()
                    .status(INITKVSERVER)
                    .ringMetadata(ringMetadata)
                    .targetServerInfo(ringMetadata.findServerInfoByHash(targetedNode.getHashKey()))
                    .build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            KVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            communicationApi.disconnect();
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            } else {
                targetedNode.setMetadataStatus(SYNCHRONIZED);
            }

        } catch (UnableToConnectException | UnableToSendContentToServerException |
                ConnectionClosedException | DeserializationException |
                UnableToDisconnectException ex) {
            throw new ClientSideException(errorMessage, ex);
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
