package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.commons.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.helper.KVEntryDeserializer;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVMessage}.
 * 
 * @author Benoit
 */
public class KVMessageDeserializer implements IMessageDeserializer<KVMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 2;
    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_KVENTRY_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(KVMessageDeserializer.class);

    private IDeserializer<KVEntry, String> kvEntryDeserializer = new KVEntryDeserializer();

    @Override
    public KVMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVMessage from byte[].");

        // remove prefix and postfix
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        serializedMessageStr = serializedMessageStr.replace(KVMessageSerializer.PREFIX, "");
        serializedMessageStr = serializedMessageStr.replace(KVMessageSerializer.POSTFIX, "");

        // raw message split
        String[] messageParts = serializedMessageStr.split(KVMessageSerializer.SEPARATOR);

        // length check
        if (messageParts.length != NUMBER_OF_MESSAGE_PARTS) {
            throw new DeserializationException(
                    CustomStringJoiner.join("", "Message must consist of exactly ",
                            String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts."));
        }

        try {
            // raw fields
            String statusStr = messageParts[MESSAGE_STATUS_INDEX];
            String kvEntryStr = messageParts[MESSAGE_KVENTRY_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            KVEntry entry = kvEntryDeserializer.deserialize(kvEntryStr);

            // deserialized object
            KVMessage deserialized = new KVMessage.Builder().status(status).key(entry.getKey())
                    .value(entry.getValue()).build();
            LOGGER.debug(join(" ", "Deserialized KVMessage is:", deserialized.toString()));

            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
