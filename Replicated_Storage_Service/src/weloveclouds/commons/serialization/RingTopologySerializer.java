package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.SerializationConstants;
import weloveclouds.ecs.models.repository.AbstractNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;

/**
 * Created by Benoit on 2016-12-08.
 */
public class RingTopologySerializer<T extends AbstractNode> implements ISerializer<String,
        RingTopology<T>> {
    private ISerializer<String, T> nodeSerializer;

    @Inject
    public RingTopologySerializer(ISerializer<String, T> nodeSerializer) {
        this.nodeSerializer = nodeSerializer;
    }

    @Override
    public String serialize(RingTopology<T> ringTopologyToSerialize) {
        String serializedRingTopology = "";

        try {
            serializedRingTopology += SerializationConstants.TOPOLOGY_START_TOKEN;
            serializedRingTopology += SerializationConstants.ORDERED_NODES_START_TOKEN;
            for (T node : ringTopologyToSerialize.getNodes()) {
                serializedRingTopology += nodeSerializer.serialize(node);
            }
            serializedRingTopology += SerializationConstants.ORDERED_NODES_END_TOKEN;
            serializedRingTopology += SerializationConstants.TOPOLOGY_END_TOKEN;
        } catch (Exception e) {
            //log throw serialize exception
        }
        return serializedRingTopology;
    }
}
