package weloveclouds.kvstore.serialization.models;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import weloveclouds.kvstore.models.KVMessage;

/**
 * Represents a byte[] of a serialized {@link KVMessage}.
 *
 * @author Benedek
 */
public class SerializedKVMessage {

    public static Charset MESSAGE_ENCODING = StandardCharsets.US_ASCII;

    private byte[] bytes;

    public SerializedKVMessage(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
