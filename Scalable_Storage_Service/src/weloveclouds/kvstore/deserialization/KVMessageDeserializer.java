package weloveclouds.kvstore.deserialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVMessage}.
 * 
 * @author Benoit
 */
public class KVMessageDeserializer implements IMessageDeserializer<KVMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 3;

    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_KEY_INDEX = 1;
    private static final int MESSAGE_VALUE_INDEX = 2;

    private static final Logger LOGGER = Logger.getLogger(KVMessageDeserializer.class);

    @Override
    public KVMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVMessage from byte[].");

        // raw message split
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        String[] messageParts = serializedMessageStr.split(KVMessageSerializer.SEPARATOR);

        // length check
        if (messageParts.length != NUMBER_OF_MESSAGE_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Message must consist of exactly ",
                    String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts.");
            LOGGER.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        try {
            // raw fields
            String statusStr = messageParts[MESSAGE_STATUS_INDEX];
            String keyStr = messageParts[MESSAGE_KEY_INDEX];
            String valueStr = messageParts[MESSAGE_VALUE_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            String key = "null".equals(keyStr) ? null : keyStr;
            String value = "null".equals(valueStr) ? null : valueStr;

            // deserialized object
            KVMessage deserialized =
                    new KVMessage.Builder().status(status).key(key).value(value).build();
            LOGGER.debug(join(" ", "Deserialized KVMessage is:", deserialized.toString()));

            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
