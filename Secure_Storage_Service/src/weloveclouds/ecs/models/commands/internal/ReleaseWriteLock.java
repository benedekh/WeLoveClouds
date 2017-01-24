package weloveclouds.ecs.models.commands.internal;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.repository.NodeStatus;
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
public class ReleaseWriteLock extends AbstractEcsNetworkCommand<StorageNode, IKVAdminMessage> {

    protected ReleaseWriteLock(Builder releaseWriteLockBuilder) {
        this.communicationApi = releaseWriteLockBuilder.communicationApi;
        this.targetedNode = releaseWriteLockBuilder.targetedNode;
        this.messageSerializer = releaseWriteLockBuilder.messageSerializer;
        this.messageDeserializer = releaseWriteLockBuilder.messageDeserializer;
        this.errorMessage =
                StringUtils.join(" ", "Unable to release write lock on node:", targetedNode);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message = new KVAdminMessage.Builder()
                    .status(IKVAdminMessage.StatusType.UNLOCKWRITE).build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            IKVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            } else {
                targetedNode.setStatus(NodeStatus.RUNNING);
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
        return StringUtils.join(" ", "Command: ReleaseWriteLock", "Targeted node:", targetedNode);
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

        public ReleaseWriteLock build() {
            return new ReleaseWriteLock(this);
        }
    }
}
