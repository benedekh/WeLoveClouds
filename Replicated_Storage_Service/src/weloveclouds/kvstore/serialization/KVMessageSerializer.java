package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A serializer which converts a {@link KVMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedMessage, KVMessage> {

    public static final String PREFIX = "<KVMESSAGE>";
    public static final String SEPARATOR = "-ŁŁŁ-";
    public static final String POSTFIX = "</KVMESSAGE>";

    private static final Logger LOGGER = Logger.getLogger(KVMessageSerializer.class);

    private ISerializer<String, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public SerializedMessage serialize(KVMessage unserializedMessage) {
        LOGGER.debug("Serializing KVMessage.");

        // original fields
        StatusType status = unserializedMessage.getStatus();
        KVEntry entry = new KVEntry(unserializedMessage.getKey(), unserializedMessage.getValue());

        // string representation
        String kvEntryStr = kvEntrySerializer.serialize(entry);
        String statusStr = status == null ? null : status.toString();

        // merged string representation
        String serialized = join(SEPARATOR, statusStr, kvEntryStr);
        String infixed = CustomStringJoiner.join("", PREFIX, serialized, POSTFIX);

        LOGGER.debug("KVMessage serialization finished.");
        return new SerializedMessage(infixed);
    }
}
