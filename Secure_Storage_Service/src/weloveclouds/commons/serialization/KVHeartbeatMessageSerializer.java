package weloveclouds.commons.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.KVHEARTBEAT_MESSAGE;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.loadbalancer.models.IKVHeartbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * A serializer which converts a {@link IKVHeartbeatMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVHeartbeatMessageSerializer
        implements IMessageSerializer<SerializedMessage, IKVHeartbeatMessage> {

    private ISerializer<AbstractXMLNode, NodeHealthInfos> nodeNodeHealthInfosSerializer;

    @Inject
    public KVHeartbeatMessageSerializer(
            ISerializer<AbstractXMLNode, NodeHealthInfos> nodeNodeHealthInfosSerializer) {
        this.nodeNodeHealthInfosSerializer = nodeNodeHealthInfosSerializer;
    }

    @Override
    public SerializedMessage serialize(IKVHeartbeatMessage unserializedMessage) {
        return new SerializedMessage(
                new XMLRootNode.Builder().token(KVHEARTBEAT_MESSAGE)
                        .addInnerNode(nodeNodeHealthInfosSerializer
                                .serialize(unserializedMessage.getNodeHealthInfos()))
                        .build().toString());
    }
}
