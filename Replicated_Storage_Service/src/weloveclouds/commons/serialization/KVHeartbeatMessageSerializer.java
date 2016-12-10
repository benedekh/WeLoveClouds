package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.loadbalancer.models.KVHearthbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.SerializationConstants.KVHEARTHBEAT_MESSAGE;

/**
 * Created by Benoit on 2016-12-09.
 */
public class KVHeartbeatMessageSerializer implements IMessageSerializer<SerializedMessage, KVHearthbeatMessage> {
    private ISerializer<AbstractXMLNode, NodeHealthInfos> nodeHealthInfosStringSerializer;

    @Inject
    public KVHeartbeatMessageSerializer(ISerializer<AbstractXMLNode, NodeHealthInfos> nodeHealthInfosStringSerializer) {
        this.nodeHealthInfosStringSerializer = nodeHealthInfosStringSerializer;
    }

    @Override
    public SerializedMessage serialize(KVHearthbeatMessage unserializedMessage) {
        return new SerializedMessage(new XMLRootNode.Builder()
                .token(KVHEARTHBEAT_MESSAGE)
                .addInnerNode(nodeHealthInfosStringSerializer
                        .serialize(unserializedMessage.getNodeHealthInfos()))
                .build()
                .toString());
    }
}
