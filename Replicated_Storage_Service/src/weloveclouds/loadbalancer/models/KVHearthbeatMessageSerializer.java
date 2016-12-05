package weloveclouds.loadbalancer.models;

import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * Created by Benoit on 2016-12-05.
 */
public class KVHearthbeatMessageSerializer implements IMessageSerializer<SerializedMessage,
        KVHearthbeatMessage> {
    @Override
    public SerializedMessage serialize(KVHearthbeatMessage unserializedMessage) {
        return null;
    }
}
