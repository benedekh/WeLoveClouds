package weloveclouds.kvstore.serialization.models;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.KVMessageSerializer;

/**
 * Represents a byte[] of a serialized {@link KVMessage}.
 *
 * @author Benedek
 */
public class SerializedKVMessage {

    public static Charset MESSAGE_ENCODING = StandardCharsets.US_ASCII;

    private byte[] bytes;

    /**
     * @param serializedMessage {@link KVMessage} serialized as a string, using
     *        {@link KVMessageSerializer#serialize(KVMessage)}.
     */
    public SerializedKVMessage(String serializedMessage) {
        this.bytes = serializedMessage.getBytes(MESSAGE_ENCODING);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
