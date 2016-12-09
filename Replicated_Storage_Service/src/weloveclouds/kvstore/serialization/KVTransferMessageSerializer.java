package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.kvstore.serialization.helper.MovableStorageUnitsSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A serializer which converts a {@link KVTransferMessage} to a {@link SerializedMessage}.
 * 
 * @author Benedek
 */
public class KVTransferMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVTransferMessage> {

    public static final String PREFIX = "<KVTRANSFERMESSAGE>";
    public static final String SEPARATOR = "-ŁŁŁ-";
    public static final String POSTFIX = "</KVTRANSFERMESSAGE>";

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageSerializer.class);

    private ISerializer<String, MovableStorageUnits> storageUnitsSerializer =
            new MovableStorageUnitsSerializer();
    private ISerializer<String, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public SerializedMessage serialize(KVTransferMessage unserializedMessage) {
        LOGGER.debug("Serializing transfer message.");

        // original fields
        StatusType status = unserializedMessage.getStatus();
        MovableStorageUnits storageUnits = unserializedMessage.getStorageUnits();
        KVEntry putableEntry = unserializedMessage.getPutableEntry();
        String removableKey = unserializedMessage.getRemovableKey();
        String responseMessage = unserializedMessage.getResponseMessage();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String storageUnitsStr = storageUnitsSerializer.serialize(storageUnits);
        String putableEntryStr = kvEntrySerializer.serialize(putableEntry);

        // merged string representation
        String serialized = join(SEPARATOR, statusStr, storageUnitsStr, putableEntryStr,
                removableKey, responseMessage);
        String prefixed = CustomStringJoiner.join("", PREFIX, serialized);
        String postfixed = CustomStringJoiner.join("", prefixed, POSTFIX);

        LOGGER.debug("Transfer message serialization finished.");
        return new SerializedMessage(postfixed);
    }

}
