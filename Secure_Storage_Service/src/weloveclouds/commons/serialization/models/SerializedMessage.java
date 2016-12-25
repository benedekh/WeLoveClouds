package weloveclouds.commons.serialization.models;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import weloveclouds.commons.serialization.IMessageSerializer;

/**
 * Represents a byte[] of a serialized message.
 *
 * @author Benedek
 */
public class SerializedMessage {

    public static Charset MESSAGE_ENCODING = StandardCharsets.UTF_8;

    private byte[] bytes;

    /**
     * @param serializedMessage message serialized as a string, using
     *        {@link IMessageSerializer#serialize(Object)}.
     */
    public SerializedMessage(String serializedMessage) {
        this.bytes = serializedMessage.getBytes(MESSAGE_ENCODING);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
