package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.commons.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.helper.KVEntryDeserializer;
import weloveclouds.kvstore.deserialization.helper.MovableStorageUnitsSetDeserializer;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public class KVTransferMessageDeserializer
        implements IMessageDeserializer<KVTransferMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 5;
    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_STORAGE_UNITS_INDEX = 1;
    private static final int MESSAGE_PUTABLE_ENTRY_INDEX = 2;
    private static final int MESSAGE_REMOVABLE_KEY_INDEX = 3;
    private static final int MESSAGE_RESPONSE_MESSAGE_INDEX = 4;

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageDeserializer.class);

    private IDeserializer<Set<MovableStorageUnit>, String> storageUnitsDeserializer =
            new MovableStorageUnitsSetDeserializer();

    private IDeserializer<KVEntry, String> kvEntryDeserializer = new KVEntryDeserializer();

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
            throw new DeserializationException(
                    CustomStringJoiner.join("", "Message must consist of exactly ",
                            String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts."));
        }

        try {
            // raw fields
            String statusStr = messageParts[MESSAGE_STATUS_INDEX];
            String storageUnitsStr = messageParts[MESSAGE_STORAGE_UNITS_INDEX];
            String putableEntryStr = messageParts[MESSAGE_PUTABLE_ENTRY_INDEX];
            String removableKeyStr = messageParts[MESSAGE_REMOVABLE_KEY_INDEX];
            String responseMessageStr = messageParts[MESSAGE_RESPONSE_MESSAGE_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            Set<MovableStorageUnit> storageUnits =
                    storageUnitsDeserializer.deserialize(storageUnitsStr);
            KVEntry putableEntry = kvEntryDeserializer.deserialize(putableEntryStr);
            String removableKey = "null".equals(removableKeyStr) ? null : removableKeyStr;
            String responseMessage = "null".equals(responseMessageStr) ? null : responseMessageStr;

            // deserialized object
            KVTransferMessage deserialized = new KVTransferMessage.Builder().status(status)
                    .storageUnits(storageUnits).putableEntry(putableEntry)
                    .removableKey(removableKey).responseMessage(responseMessage).build();

            LOGGER.debug("KVTransferMessage deserialization finished.");
            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }

}
