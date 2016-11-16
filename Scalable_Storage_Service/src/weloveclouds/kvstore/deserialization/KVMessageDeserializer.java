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

    private static int NUMBER_OF_MESSAGE_PARTS = 3;

    private static int MESSAGE_STATUS_INDEX = 0;
    private static int MESSAGE_KEY_INDEX = 1;
    private static int MESSAGE_VALUE_INDEX = 2;

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public KVMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        logger.debug("Deserializing KVMessage from byte[].");

        // raw message split
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        String[] messageParts = serializedMessageStr.split(KVMessageSerializer.SEPARATOR);

        // length check
        if (messageParts.length != NUMBER_OF_MESSAGE_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Message must consist of exactly ",
                    String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts.");
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        try {
            // raw fields
            String statusStr = messageParts[MESSAGE_STATUS_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            String key = messageParts[MESSAGE_KEY_INDEX];
            String value = messageParts[MESSAGE_VALUE_INDEX];

            // deserialized object
            KVMessage deserialized =
                    new KVMessage.KVMessageBuilder().status(status).key(key).value(value).build();
            logger.debug(join(" ", "Deserialized KVMessage is:", deserialized.toString()));

            return deserialized;
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
