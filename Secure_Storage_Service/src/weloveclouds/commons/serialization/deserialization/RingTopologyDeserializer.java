package weloveclouds.commons.serialization.deserialization;

import static weloveclouds.commons.serialization.models.XMLTokens.NODE;
import static weloveclouds.commons.serialization.models.XMLTokens.ORDERED_NODES;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.ecs.models.repository.AbstractNode;
import weloveclouds.ecs.models.topology.RingTopology;

/**
 * A deserializer which converts a {@link String} to a {@link RingTopology<T>}.
 * 
 * @author Benoit
 */
public class RingTopologyDeserializer<T extends AbstractNode>
        implements IDeserializer<RingTopology<T>, String> {
    private IDeserializer<T, String> nodeDeserializer;

    @Inject
    public RingTopologyDeserializer(IDeserializer<T, String> nodeDeserializer) {
        this.nodeDeserializer = nodeDeserializer;
    }

    @Override
    public RingTopology<T> deserialize(String serializedRingTopology)
            throws DeserializationException {
        Matcher orderedNodesMatcher =
                getRegexFromToken(ORDERED_NODES).matcher(serializedRingTopology);
        List<T> nodesList = new ArrayList<>();

        if (orderedNodesMatcher.find()) {
            Matcher nodeMatcher =
                    getRegexFromToken(NODE).matcher(orderedNodesMatcher.group(XML_NODE));
            while (nodeMatcher.find()) {
                nodesList.add(nodeDeserializer.deserialize(nodeMatcher.group(XML_NODE)));
            }
        } else {
            throw new DeserializationException(
                    "Unable to deserialize ring topology: " + serializedRingTopology);
        }
        return new RingTopology<>(nodesList);
    }


}
