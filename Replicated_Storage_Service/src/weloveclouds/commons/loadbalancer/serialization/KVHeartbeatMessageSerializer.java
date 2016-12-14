package weloveclouds.commons.loadbalancer.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.loadbalancer.models.KVHeartbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.XMLTokens.KVHEARTBEAT_MESSAGE;

/**
 * Created by Benoit on 2016-12-09.
 */
public class KVHeartbeatMessageSerializer implements
        IMessageSerializer<SerializedMessage, KVHeartbeatMessage> {
    private ISerializer<AbstractXMLNode, NodeHealthInfos> nodeHealthInfosSerializer;

    @Inject
    public KVHeartbeatMessageSerializer(ISerializer<AbstractXMLNode, NodeHealthInfos>
                                                nodeHealthInfosSerializer) {
        this.nodeHealthInfosSerializer = nodeHealthInfosSerializer;
    }

    @Override
    public SerializedMessage serialize(KVHeartbeatMessage unserializedMessage) {
        return new SerializedMessage(new XMLRootNode.Builder()
                .token(KVHEARTBEAT_MESSAGE)
                .addInnerNode(nodeHealthInfosSerializer
                        .serialize(unserializedMessage.getNodeHealthInfos()))
                .build()
                .toString());
    }
}
