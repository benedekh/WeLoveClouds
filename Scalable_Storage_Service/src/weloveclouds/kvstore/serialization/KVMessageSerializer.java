package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;

/**
 * An exact deserializer which converts a {@link KVMessage} to a {@link SerializedKVMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedKVMessage, KVMessage> {

    private static final String SEPARATOR = "-\r-";

    private static final Logger LOGGER = Logger.getLogger(KVMessageSerializer.class);

    @Override
    public SerializedKVMessage serialize(KVMessage unserializedMessage) {
        LOGGER.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        StatusType status = unserializedMessage.getStatus();
        String statusStr = status == null ? null : status.toString();
        String serialized = join(SEPARATOR, statusStr, unserializedMessage.getKey(),
                unserializedMessage.getValue());

        return new SerializedKVMessage(serialized);
    }
}
