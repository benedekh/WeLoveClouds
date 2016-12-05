package weloveclouds.loadbalancer.models;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * Created by Benoit on 2016-12-05.
 */
public class KVHearthbeatMessageDeserializer implements IMessageDeserializer<KVHearthbeatMessage,
        SerializedMessage> {
    @Override
    public KVHearthbeatMessage deserialize(SerializedMessage serializedMessage) throws DeserializationException {
        return null;
    }

    @Override
    public KVHearthbeatMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        return null;
    }
}
