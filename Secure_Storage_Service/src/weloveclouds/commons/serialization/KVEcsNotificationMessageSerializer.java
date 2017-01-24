package weloveclouds.commons.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.KVECS_NOTIFICATION_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.UNRESPONSIVE_NODES_NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.UNRESPONSIVE_NODE_NAME;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * A serializer which converts a {@link IKVEcsNotificationMessage} to a {@link SerializedMessage}.
 *
 * @author Benoit
 */
public class KVEcsNotificationMessageSerializer
        implements IMessageSerializer<SerializedMessage, IKVEcsNotificationMessage> {
    ISerializer<AbstractXMLNode, RingTopology<StorageNode>> ringTopologySerializer;
    ISerializer<AbstractXMLNode, NodeHealthInfos> nodeNodeHealthInfosSerializer;

    @Inject
    public KVEcsNotificationMessageSerializer(
            ISerializer<AbstractXMLNode, RingTopology<StorageNode>> ringTopologySerializer,
            ISerializer<AbstractXMLNode, NodeHealthInfos> nodeNodeHealthInfosSerializer) {
        this.ringTopologySerializer = ringTopologySerializer;
        this.nodeNodeHealthInfosSerializer = nodeNodeHealthInfosSerializer;
    }

    @Override
    public SerializedMessage serialize(IKVEcsNotificationMessage unserializedMessage) {
        XMLRootNode.Builder xmlBuilder = new XMLRootNode.Builder().token(KVECS_NOTIFICATION_MESSAGE)
                .addInnerNode(new XMLNode(STATUS, unserializedMessage.getStatus().name()));

        if (unserializedMessage.getNodeHealthInfos() != null) {
            xmlBuilder.addInnerNode(nodeNodeHealthInfosSerializer
                    .serialize(unserializedMessage.getNodeHealthInfos()));
        }
        if (unserializedMessage.getRingTopology() != null) {
            xmlBuilder.addInnerNode(
                    ringTopologySerializer.serialize(unserializedMessage.getRingTopology()));
        }
        if (!unserializedMessage.getUnresponsiveNodeNames().isEmpty()) {
            XMLRootNode.Builder unresponsiveNodesName =
                    new XMLRootNode.Builder().token(UNRESPONSIVE_NODES_NAME);
            for (String unresponsiveNodeName : unserializedMessage.getUnresponsiveNodeNames()) {
                unresponsiveNodesName.addInnerNode(new XMLNode(UNRESPONSIVE_NODE_NAME,
                        unresponsiveNodeName));
            }
            xmlBuilder.addInnerNode(unresponsiveNodesName.build());
        }

        return new SerializedMessage(xmlBuilder.build().toString());
    }
}
