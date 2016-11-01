package weloveclouds.kvstore.serialization;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import weloveclouds.kvstore.IKVMessage.StatusType;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;

/**
 * Created by Benoit on 2016-11-01.
 */
public class KVMessageDeserializer implements IMessageDeserializer<KVMessage, SerializedKVMessage> {
    public static Charset MESSAGE_ENCODING = StandardCharsets.US_ASCII;
    private static String SEPARATOR = "-\r-";
    private static int NUMBER_OF_MESSAGE_PARTS = 3;
    private static int MESSAGE_STATUS_INDEX = 0;
    private static int MESSAGE_KEY_INDEX = 1;
    private static int MESSAGE_VALUE_INDEX = 2;

    @Override
    public KVMessage deserialize(SerializedKVMessage serializedMessage) throws DeserializationException{
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        String serializedMessageAsString = new String(serializedMessage, MESSAGE_ENCODING);
        String[] messageParts = serializedMessageAsString.split(SEPARATOR);

        if (messageParts.length < NUMBER_OF_MESSAGE_PARTS) {
            throw new DeserializationException("Message contains more than three parts.");
        }

        try {
            StatusType status = StatusType.valueOf(messageParts[MESSAGE_STATUS_INDEX]);
            String key = messageParts[MESSAGE_KEY_INDEX];
            String value =
                    messageParts[MESSAGE_VALUE_INDEX].equals("null") ? null : messageParts[MESSAGE_VALUE_INDEX];

            return new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
        } catch (IllegalArgumentException ex) {
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
