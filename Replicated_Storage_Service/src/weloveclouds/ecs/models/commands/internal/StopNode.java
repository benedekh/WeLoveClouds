package weloveclouds.ecs.models.commands.internal;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.HALTED;
import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;
import static weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;

/**
 * Created by Benoit on 2016-11-20.
 */
public class StopNode extends AbstractEcsNetworkCommand {

    public StopNode(Builder stopNodeBuilder) {
        this.communicationApi = stopNodeBuilder.communicationApi;
        this.targetedNode = stopNodeBuilder.targetedNode;
        this.messageSerializer = stopNodeBuilder.messageSerializer;
        this.messageDeserializer = stopNodeBuilder.messageDeserializer;
        this.errorMessage = CustomStringJoiner.join(" ", "Unable to stop node:",
                targetedNode.toString());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(targetedNode.getEcsChannelConnectionInfo());
            KVAdminMessage message = new KVAdminMessage.Builder()
                    .status(StatusType.STOP)
                    .build();
            communicationApi.send(messageSerializer.serialize(message).getBytes());
            KVAdminMessage response = messageDeserializer.deserialize(communicationApi.receive());
            if (response.getStatus() != RESPONSE_SUCCESS) {
                throw new ClientSideException(errorMessage);
            }else{
                targetedNode.setStatus(INITIALIZED);
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
        return CustomStringJoiner.join(" ", "Command: StopNode", "Targeted node:", targetedNode
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

        public StopNode build() {
            return new StopNode(this);
        }
    }
}
