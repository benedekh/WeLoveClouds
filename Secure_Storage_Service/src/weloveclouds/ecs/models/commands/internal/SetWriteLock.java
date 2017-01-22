package weloveclouds.ecs.models.commands.internal;

import static weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.ecs.models.repository.StorageNode;

import static weloveclouds.ecs.models.repository.NodeStatus.WRITE_LOCKED;

/**
 * Created by Benoit on 2016-11-22.
 */
public class SetWriteLock extends AbstractEcsNetworkCommand<StorageNode, IKVAdminMessage> {

    protected SetWriteLock(Builder setWriteLockBuilder) {
        this.communicationApi = setWriteLockBuilder.communicationApi;
        this.targetedNode = setWriteLockBuilder.targetedNode;
        this.messageSerializer = setWriteLockBuilder.messageSerializer;
        this.messageDeserializer = setWriteLockBuilder.messageDeserializer;
        this.errorMessage =
                StringUtils.join(" ", "Unable to set write lock on node:", targetedNode);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message =
                    new KVAdminMessage.Builder().status(StatusType.LOCKWRITE).build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            IKVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            } else {
                targetedNode.setStatus(WRITE_LOCKED);
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
        return StringUtils.join(" ", "Command: SetWriteLock", "Targeted node:", targetedNode);
    }

    public static class Builder {
        private ICommunicationApi communicationApi;
        private StorageNode targetedNode;
        private IMessageSerializer<SerializedMessage, IKVAdminMessage> messageSerializer;
        private IMessageDeserializer<IKVAdminMessage, SerializedMessage> messageDeserializer;

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

        public SetWriteLock build() {
            return new SetWriteLock(this);
        }
    }
}
