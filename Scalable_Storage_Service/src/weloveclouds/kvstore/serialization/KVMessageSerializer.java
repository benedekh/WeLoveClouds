package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A serializer which converts a {@link KVMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedMessage, KVMessage> {

    public static final String SEPARATOR = "-\r\r-";

    private static final Logger LOGGER = Logger.getLogger(KVMessageSerializer.class);

    @Override
    public SerializedMessage serialize(KVMessage unserializedMessage) {
        LOGGER.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        // original fields
        StatusType status = unserializedMessage.getStatus();
        String key = unserializedMessage.getKey();
        String value = unserializedMessage.getValue();

        // string representation
        String statusStr = status == null ? null : status.toString();

        // merged string representation
        String serialized = join(SEPARATOR, statusStr, key, value);

        LOGGER.debug(join(" ", "Serialized message:", serialized));
        return new SerializedMessage(serialized);
    }
}
