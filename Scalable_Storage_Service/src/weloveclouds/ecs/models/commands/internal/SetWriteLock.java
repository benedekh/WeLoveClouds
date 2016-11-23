package weloveclouds.ecs.models.commands.internal;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.repository.StorageNodeStatus;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.WRITELOCKED;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

/**
 * Created by Benoit on 2016-11-22.
 */
public class SetWriteLock extends AbstractEcsNetworkCommand {

    public SetWriteLock(Builder setWriteLockBuilder) {
        this.communicationApi = setWriteLockBuilder.communicationApi;
        this.targetedNode = setWriteLockBuilder.targetedNode;
        this.messageSerializer = setWriteLockBuilder.messageSerializer;
        this.messageDeserializer = setWriteLockBuilder.messageDeserializer;
        this.errorMessage = CustomStringJoiner.join(" ", "Unable to set write lock on node:",
                targetedNode.toString());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message = new KVAdminMessage.Builder()
                    .status(StatusType.LOCKWRITE)
                    .build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            KVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            communicationApi.disconnect();
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            } else {
                targetedNode.setStatus(WRITELOCKED);
            }
        } catch (UnableToConnectException | UnableToSendContentToServerException |
                ConnectionClosedException | DeserializationException |
                UnableToDisconnectException ex) {
            throw new ClientSideException(errorMessage, ex);
        }
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "Command: SetWriteLock", "Targeted node:", targetedNode
                .toString());
    }

    public static class Builder {
        private ICommunicationApi communicationApi;
        private StorageNode targetedNode;
        private IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer;
        private IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer;

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

        public SetWriteLock build() {
            return new SetWriteLock(this);
        }
    }
}
