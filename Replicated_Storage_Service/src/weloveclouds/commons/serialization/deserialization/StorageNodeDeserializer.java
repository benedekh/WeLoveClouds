package weloveclouds.commons.serialization.deserialization;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

/**
 * Created by Benoit on 2016-12-09.
 */
public class StorageNodeDeserializer implements IDeserializer<StorageNode, String>{
    @Override
    public StorageNode deserialize(String from) throws DeserializationException {
        return null;
    }
}
