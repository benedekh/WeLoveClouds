package weloveclouds.kvstore.serialization.models;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
