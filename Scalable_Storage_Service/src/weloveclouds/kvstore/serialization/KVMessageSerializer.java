package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A serializer which converts a {@link KVMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedMessage, KVMessage> {

    public static final String PREFIX = "<KVMESSAGE>";
    public static final String SEPARATOR = "-\r\r-";
    public static final String POSTFIX = "</KVMESSAGE>";

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
        String prefixed = CustomStringJoiner.join("", PREFIX, serialized);
        String postfixed = CustomStringJoiner.join("", prefixed, POSTFIX);

        LOGGER.debug(join(" ", "Serialized message:", postfixed));
        return new SerializedMessage(postfixed);
    }
}
