package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.nio.charset.StandardCharsets;

import weloveclouds.kvstore.IKVMessage.StatusType;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;

public class SerializedKVMessage {

    private static String SEPARATOR = "-\r-";
    private static int NUMBER_OF_MESSAGE_PARTS = 3;

    private static int MESSAGE_STATUS_INDEX = 0;
    private static int MESSAGE_KEY_INDEX = 1;
    private static int MESSAGE_VALUE_INDEX = 2;

    private KVMessage message;

    public SerializedKVMessage(KVMessage message) {
        this.message = message;
    }

    public KVMessage getMessage() {
        return message;
    }

    public byte[] toByteArray() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        StatusType status = message.getStatus();
        String statusStr = status == null ? null : status.toString();
        return join(SEPARATOR, statusStr, message.getKey(), message.getValue());
    }

    public static SerializedKVMessage fromByteArray(byte[] array) throws DeserializationException {
        String serialized = new String(array, StandardCharsets.UTF_8);
        String[] parts = serialized.split(SEPARATOR);

        if (parts.length < NUMBER_OF_MESSAGE_PARTS) {
            throw new DeserializationException("Message contains more than three parts.");
        }

        try {
            StatusType status = StatusType.valueOf(parts[MESSAGE_STATUS_INDEX]);
            String key = parts[MESSAGE_KEY_INDEX];
            String value =
                    parts[MESSAGE_VALUE_INDEX].equals("null") ? null : parts[MESSAGE_VALUE_INDEX];

            KVMessage message =
                    new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();

            return new SerializedKVMessage(message);
        } catch (IllegalArgumentException ex) {
            throw new DeserializationException("StatusType is not recognized.");
        }
    }

}
