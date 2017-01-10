package weloveclouds.ecs.models.commands.internal;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.commands.AbstractCommand;

/**
 * Created by Benoit on 2016-11-22.
 */
public abstract class AbstractEcsNetworkCommand<T, M> extends AbstractCommand<T> {
    protected ICommunicationApi communicationApi;
    protected IMessageSerializer<SerializedMessage, M> messageSerializer;
    protected IMessageDeserializer<M, SerializedMessage> messageDeserializer;
    protected T targetedNode;
    protected String errorMessage;
}
