package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * An exact serializer which converts a {@link KVMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedMessage, KVMessage> {

    private static final String SEPARATOR = "-\r-";

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public SerializedMessage serialize(KVMessage unserializedMessage) {
        logger.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        // original fields
        StatusType status = unserializedMessage.getStatus();
        String key = unserializedMessage.getKey();
        String value = unserializedMessage.getValue();

        // string representation
        String statusStr = status == null ? null : status.toString();

        // merged string representation
        String serialized = join(SEPARATOR, statusStr, key, value);

        logger.debug(join(" ", "Serialized message:", serialized));
        return new SerializedMessage(serialized);
    }
}
