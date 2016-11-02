package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;

/**
 * Created by Benoit on 2016-11-01.
 */
public class KVMessageDeserializer implements IMessageDeserializer<KVMessage, SerializedKVMessage> {
    private static String SEPARATOR = "-\r-";
    private static int NUMBER_OF_MESSAGE_PARTS = 3;
    private static int MESSAGE_STATUS_INDEX = 0;
    private static int MESSAGE_KEY_INDEX = 1;
    private static int MESSAGE_VALUE_INDEX = 2;

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public KVMessage deserialize(SerializedKVMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        logger.debug("Deserializing message from byte[].");

        String serializedMessageAsString =
                new String(serializedMessage, SerializedKVMessage.MESSAGE_ENCODING);
        String[] messageParts = serializedMessageAsString.split(SEPARATOR);

        if (messageParts.length > NUMBER_OF_MESSAGE_PARTS) {
            String errorMessage = "Message contains more than three parts.";
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        try {
            StatusType status = StatusType.valueOf(messageParts[MESSAGE_STATUS_INDEX]);
            String key = messageParts[MESSAGE_KEY_INDEX];
            String value = messageParts[MESSAGE_VALUE_INDEX].equals("null") ? null
                    : messageParts[MESSAGE_VALUE_INDEX];

            KVMessage deserialized =
                    new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
            logger.debug(join(" ", "Deserialized message is:", deserialized.toString()));

            return deserialized;
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
