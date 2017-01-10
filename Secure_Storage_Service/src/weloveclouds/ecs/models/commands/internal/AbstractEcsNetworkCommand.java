package weloveclouds.ecs.models.commands.internal;

import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.repository.StorageNode;


/**
 * Created by Benoit on 2016-11-22.
 */
public abstract class AbstractEcsNetworkCommand extends AbstractCommand<StorageNode> {
    protected ICommunicationApi communicationApi;
    protected IMessageSerializer<SerializedMessage, IKVAdminMessage> messageSerializer;
    protected IMessageDeserializer<IKVAdminMessage, SerializedMessage> messageDeserializer;
    protected StorageNode targetedNode;
    protected String errorMessage;
}
