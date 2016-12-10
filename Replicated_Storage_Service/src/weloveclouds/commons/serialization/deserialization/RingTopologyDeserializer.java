package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import weloveclouds.ecs.models.repository.AbstractNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_END_TOKEN;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_START_TOKEN;
import static weloveclouds.commons.serialization.models.SerializationConstants.ORDERED_NODES_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.ORDERED_NODES_REGEX;

/**
 * Created by Benoit on 2016-12-09.
 */
public class RingTopologyDeserializer<T extends AbstractNode> implements IDeserializer<RingTopology<T>,
        String> {
    private IDeserializer<T, String> nodeDeserializer;

    @Inject
    public RingTopologyDeserializer(IDeserializer<T, String> nodeDeserializer) {
        this.nodeDeserializer = nodeDeserializer;
    }

    @Override
    public RingTopology<T> deserialize(String serializedRingTopology) throws
            DeserializationException {
        Matcher orderedNodes = ORDERED_NODES_REGEX.matcher(serializedRingTopology);
        List<T> nodesList = new ArrayList<>();

        if (orderedNodes.find()) {
            Matcher nodeMatcher = NODE_REGEX.matcher(orderedNodes.group(ORDERED_NODES_GROUP));
            while (nodeMatcher.find()) {
                nodesList.add(nodeDeserializer.deserialize(nodeMatcher.group(NODE_GROUP)));
            }
        } else {
            throw new DeserializationException("Unable to deserialize ring topology: " +
                    serializedRingTopology);
        }
        return new RingTopology<>(nodesList);
    }


}
