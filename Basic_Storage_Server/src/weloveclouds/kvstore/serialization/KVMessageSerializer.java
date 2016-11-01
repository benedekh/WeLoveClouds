package weloveclouds.kvstore.serialization;

import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;
import weloveclouds.kvstore.utils.KVMessageUtils;

/**
 * Created by Benoit on 2016-11-01.
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedKVMessage, KVMessage> {

    @Override
    public SerializedKVMessage serialize(KVMessage unserializedMessage) {
        return new SerializedKVMessage(
                KVMessageUtils.convertMessageToString(unserializedMessage).getBytes());
    }
}
