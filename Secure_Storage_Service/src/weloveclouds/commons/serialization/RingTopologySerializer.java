package weloveclouds.commons.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.ORDERED_NODES;
import static weloveclouds.commons.serialization.models.XMLTokens.TOPOLOGY;

import java.util.List;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.ecs.models.repository.AbstractNode;
import weloveclouds.ecs.models.topology.RingTopology;

/**
 * A serializer which converts a {@link RingTopology<T>} to a {@link AbstractXMLNode}.
 * 
 * @author Benoit
 */
public class RingTopologySerializer<T extends AbstractNode>
        implements ISerializer<AbstractXMLNode, RingTopology<T>> {
    private ISerializer<AbstractXMLNode, T> nodeSerializer;

    @Inject
    public RingTopologySerializer(ISerializer<AbstractXMLNode, T> nodeSerializer) {
        this.nodeSerializer = nodeSerializer;
    }

    @Override
    public AbstractXMLNode serialize(RingTopology<T> ringTopologyToSerialize) {
        return new XMLRootNode.Builder()
                .token(TOPOLOGY)
                .addInnerNode(serializeTopologyNodes(ringTopologyToSerialize.getNodes()))
                .build();
    }

    public AbstractXMLNode serializeTopologyNodes(List<T> nodes) {
        XMLRootNode.Builder orderedNodes = new XMLRootNode.Builder().token(ORDERED_NODES);
        for (T node : nodes) {
            orderedNodes.addInnerNode(nodeSerializer.serialize(node));
        }
        return orderedNodes.build();
    }
}
