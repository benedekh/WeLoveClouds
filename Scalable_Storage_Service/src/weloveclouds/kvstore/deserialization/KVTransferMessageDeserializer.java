package weloveclouds.kvstore.deserialization;

import static weloveclouds.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.MovableStorageUnitsDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public class KVTransferMessageDeserializer
        implements IMessageDeserializer<KVTransferMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 3;

    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_STORAGE_UNITS_INDEX = 1;
    private static final int MESSAGE_RESPONSE_MESSAGE_INDEX = 2;

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageDeserializer.class);

    private IDeserializer<MovableStorageUnits, String> storageUnitsDeserializer =
            new MovableStorageUnitsDeserializer();

    @Override
    public KVTransferMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVTransferMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVTransferMessage from byte[].");

        // remove prefix and postfix
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        serializedMessageStr = serializedMessageStr.replace(KVTransferMessageSerializer.PREFIX, "");
        serializedMessageStr =
                serializedMessageStr.replace(KVTransferMessageSerializer.POSTFIX, "");

        // raw message split
        String[] messageParts = serializedMessageStr.split(KVTransferMessageSerializer.SEPARATOR);

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
            String storageUnitsStr = messageParts[MESSAGE_STORAGE_UNITS_INDEX];
            String responseMessageStr = messageParts[MESSAGE_RESPONSE_MESSAGE_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            MovableStorageUnits storageUnits =
                    storageUnitsDeserializer.deserialize(storageUnitsStr);
            String responseMessage = "null".equals(responseMessageStr) ? null : responseMessageStr;;

            // deserialized object
            KVTransferMessage deserialized = new KVTransferMessage.Builder().status(status)
                    .storageUnits(storageUnits).responseMessage(responseMessage).build();

            LOGGER.debug("KVTransferMessage deserialization finished.");
            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }

}
