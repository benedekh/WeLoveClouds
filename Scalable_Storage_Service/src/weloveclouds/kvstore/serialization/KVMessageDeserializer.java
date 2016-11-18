package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;

/**
 * An exact deserializer which converts a {@link SerializedKVMessage} to a {@link KVMessage}.
 * 
 * @author Benoit
 */
public class KVMessageDeserializer implements IMessageDeserializer<KVMessage, SerializedKVMessage> {
    private static String SEPARATOR = "-\r-";
    private static int NUMBER_OF_MESSAGE_PARTS = 3;
    private static int MESSAGE_STATUS_INDEX = 0;
    private static int MESSAGE_KEY_INDEX = 1;
    private static int MESSAGE_VALUE_INDEX = 2;

    private static final Logger LOGGER = Logger.getLogger(KVMessageDeserializer.class);

    @Override
    public KVMessage deserialize(SerializedKVMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing message from byte[].");

        String serializedMessageAsString =
                new String(serializedMessage, SerializedKVMessage.MESSAGE_ENCODING);
        String[] messageParts = serializedMessageAsString.split(SEPARATOR);

        if (messageParts.length > NUMBER_OF_MESSAGE_PARTS) {
            String errorMessage = "Message contains more than three parts.";
            LOGGER.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        try {
            StatusType status = StatusType.valueOf(messageParts[MESSAGE_STATUS_INDEX]);
            String key = messageParts[MESSAGE_KEY_INDEX];
            String value = messageParts[MESSAGE_VALUE_INDEX].equals("null") ? null
                    : messageParts[MESSAGE_VALUE_INDEX];

            KVMessage deserialized =
                    new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
            LOGGER.debug(join(" ", "Deserialized message is:", deserialized.toString()));

            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
