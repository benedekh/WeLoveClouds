package weloveclouds.commons.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitsSetSerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;


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

    private ISerializer<String, Set<MovableStorageUnit>> storageUnitsSerializer =
            new MovableStorageUnitsSetSerializer();
    private ISerializer<String, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public SerializedMessage serialize(KVTransferMessage unserializedMessage) {
        LOGGER.debug("Serializing KVTransferMessage.");

        // original fields
        StatusType status = unserializedMessage.getStatus();
        Set<MovableStorageUnit> storageUnits = unserializedMessage.getStorageUnits();
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
        String infixed = CustomStringJoiner.join("", PREFIX, serialized, POSTFIX);

        LOGGER.debug("KVTransferMessage serialization finished.");
        return new SerializedMessage(infixed);
    }

}
