package weloveclouds.ecs.models.commands.internal;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;


/**
 * Created by Benoit on 2016-11-22.
 */
public abstract class AbstractEcsNetworkCommand extends AbstractCommand<StorageNode> {
    protected ICommunicationApi communicationApi;
    protected IMessageSerializer<SerializedMessage, KVAdminMessage> messageSerializer;
    protected IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer;
    protected StorageNode targetedNode;
    protected String errorMessage;
}
