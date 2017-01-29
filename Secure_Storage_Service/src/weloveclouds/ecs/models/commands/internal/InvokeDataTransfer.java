package weloveclouds.ecs.models.commands.internal;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

import static weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

/**
 * Created by Benoit on 2016-11-23.
 */
public class InvokeDataTransfer extends AbstractEcsNetworkCommand<StorageNode, IKVAdminMessage> {
    private StorageNode newNode;
    private RingMetadata ringMetadata;
    private boolean isNodeRemoval;

    protected InvokeDataTransfer(Builder invokeDataTransferBuilder) {
        this.communicationApi = invokeDataTransferBuilder.communicationApi;
        this.targetedNode = invokeDataTransferBuilder.targetedNode;
        this.messageSerializer = invokeDataTransferBuilder.messageSerializer;
        this.messageDeserializer = invokeDataTransferBuilder.messageDeserializer;
        this.newNode = invokeDataTransferBuilder.newNode;
        this.ringMetadata = invokeDataTransferBuilder.ringMetadata;
        this.isNodeRemoval = invokeDataTransferBuilder.isNodeRemoval;
        this.errorMessage =
                StringUtils.join(" ", "Unable to invoke data transfer on node:", targetedNode);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message =
                    new KVAdminMessage.Builder().status(IKVAdminMessage.StatusType.MOVEDATA)
                            .targetServerInfo(
                                    ringMetadata.findServerInfoByHash(newNode.getHashKey()))
                            .build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            IKVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            }else if(isNodeRemoval){
                targetedNode.setStatus(NodeStatus.IDLE);
                targetedNode.clearHashRange();
            }
        } catch (ClientSideException | DeserializationException ex) {
            throw new ClientSideException(errorMessage, ex);
        } finally {
            try {
                communicationApi.disconnect();
            } catch (UnableToDisconnectException ex) {
                // LOG
            }
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "Command: InvokeDataTransfer", "Targeted node:", targetedNode);
    }

    public static class Builder {
        private ICommunicationApi communicationApi;
        private StorageNode targetedNode;
        private IMessageSerializer<SerializedMessage, IKVAdminMessage> messageSerializer;
        private IMessageDeserializer<IKVAdminMessage, SerializedMessage> messageDeserializer;
        private RingMetadata ringMetadata;
        private StorageNode newNode;
        private boolean isNodeRemoval;

        public Builder() {
            isNodeRemoval = false;
        }

        public Builder isNodeRemoval(boolean isNodeRemoval) {
            this.isNodeRemoval = isNodeRemoval;
            return this;
        }

        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder targetedNode(StorageNode targetedNode) {
            this.targetedNode = targetedNode;
            return this;
        }

        public Builder messageSerializer(
                IMessageSerializer<SerializedMessage, IKVAdminMessage> messageSerializer) {
            this.messageSerializer = messageSerializer;
            return this;
        }

        public Builder messageDeserializer(
                IMessageDeserializer<IKVAdminMessage, SerializedMessage> messageDeserializer) {
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        public Builder newNode(StorageNode newNode) {
            this.newNode = newNode;
            return this;
        }

        public Builder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        public InvokeDataTransfer build() {
            return new InvokeDataTransfer(this);
        }
    }
}
