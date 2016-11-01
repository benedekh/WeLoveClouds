package weloveclouds.kvstore.serialization;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.KVMessageUtils;

/**
 * Created by Benoit on 2016-11-01.
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedKVMessage, KVMessage> {
    public static Charset MESSAGE_ENCODING = StandardCharsets.US_ASCII;


    @Override
    public SerializedKVMessage serialize(KVMessage unserializedMessage) {
        return new SerializedKVMessage(KVMessageUtils.convertMessageToString
                (unserializedMessage).getBytes());
    }
}
