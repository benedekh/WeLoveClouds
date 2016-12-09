package weloveclouds.commons.serialization.deserialization;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

/**
 * Created by Benoit on 2016-12-09.
 */
public class RingTopologyDeserializer<T> implements IDeserializer<RingTopology<T>, String> {
    private IDeserializer<StorageNode, String> nodeDeserializer;

    public RingTopologyDeserializer(IDeserializer<StorageNode, String> nodeDeserializer) {
        this.nodeDeserializer = nodeDeserializer;
    }

    @Override
    public RingTopology<T> deserialize(String from) throws DeserializationException {
        return null;
    }
}
