package weloveclouds.ecs.models.commands.internal;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

/**
 * Created by Benoit on 2016-11-23.
 */
public class InvokeDataTransfer extends AbstractEcsNetworkCommand {
    private StorageNode newNode;
    private RingMetadata ringMetadata;

    protected InvokeDataTransfer(Builder invokeDataTransferBuilder) {
        this.communicationApi = invokeDataTransferBuilder.communicationApi;
        this.targetedNode = invokeDataTransferBuilder.targetedNode;
        this.messageSerializer = invokeDataTransferBuilder.messageSerializer;
        this.messageDeserializer = invokeDataTransferBuilder.messageDeserializer;
        this.newNode = invokeDataTransferBuilder.newNode;
        this.ringMetadata = invokeDataTransferBuilder.ringMetadata;
        this.errorMessage = CustomStringJoiner.join(" ", "Unable to invoke data transfer on node:",
                targetedNode.toString());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message = new KVAdminMessage.Builder()
                    .status(IKVAdminMessage.StatusType.MOVEDATA)
                    .targetServerInfo(ringMetadata.findServerInfoByHash(newNode.getHashKey()))
                    .build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            KVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            }
        } catch (ClientSideException | DeserializationException ex) {
            throw new ClientSideException(errorMessage, ex);
        }finally {
            try {
                communicationApi.disconnect();
            }catch(UnableToDisconnectException ex){
                //LOG
            }
        }
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "Command: InvokeDataTransfer", "Targeted node:",
                targetedNode.toString());
    }

    public static class Builder {
        private ICommunicationApi communicationApi;
        private StorageNode targetedNode;
        private IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer;
        private IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer;
        private RingMetadata ringMetadata;
        private StorageNode newNode;

        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
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
