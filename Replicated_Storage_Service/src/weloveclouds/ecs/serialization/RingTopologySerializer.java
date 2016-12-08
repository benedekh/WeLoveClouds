package weloveclouds.ecs.serialization;

import com.google.inject.Inject;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.kvstore.serialization.helper.ISerializer;

import static weloveclouds.ecs.serialization.SerializationTokens.ORDERED_NODES_END_TOKEN;
import static weloveclouds.ecs.serialization.SerializationTokens.ORDERED_NODES_START_TOKEN;
import static weloveclouds.ecs.serialization.SerializationTokens.TOPOLOGY_END_TOKEN;
import static weloveclouds.ecs.serialization.SerializationTokens.TOPOLOGY_START_TOKEN;

/**
 * Created by Benoit on 2016-12-08.
 */
public class RingTopologySerializer<T> implements ISerializer<String, RingTopology<T>> {
    private ISerializer<String, T> nodeSerializer;

    @Inject
    public RingTopologySerializer(ISerializer<String, T> nodeSerializer) {
        this.nodeSerializer = nodeSerializer;
    }

    @Override
    public String serialize(RingTopology<T> ringTopologyToSerialize) {
        String serializedRingTopology = "";

        try {
            serializedRingTopology += TOPOLOGY_START_TOKEN;
            serializedRingTopology += ORDERED_NODES_START_TOKEN;
            for (T node : ringTopologyToSerialize.getNodes()) {
                serializedRingTopology += nodeSerializer.serialize(node);
            }
            serializedRingTopology += ORDERED_NODES_END_TOKEN;
            serializedRingTopology += TOPOLOGY_END_TOKEN;
        } catch (Exception e) {
            //log throw serialize exception
        }
        return serializedRingTopology;
    }
}
