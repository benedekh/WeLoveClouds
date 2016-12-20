package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import java.util.List;

import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.ecs.models.repository.AbstractNode;
import weloveclouds.ecs.models.topology.RingTopology;

import static weloveclouds.commons.serialization.models.XMLTokens.ORDERED_NODES;
import static weloveclouds.commons.serialization.models.XMLTokens.TOPOLOGY;

/**
 * Created by Benoit on 2016-12-08.
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
