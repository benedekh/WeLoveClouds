package weloveclouds.kvstore.serialization;

import org.apache.log4j.Logger;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;
import weloveclouds.kvstore.utils.KVMessageUtils;

/**
 * An exact deserializer which converts a {@link KVMessage} to a {@link SerializedKVMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedKVMessage, KVMessage> {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public SerializedKVMessage serialize(KVMessage unserializedMessage) {
        logger.debug(join(" ", "Serializing message:", unserializedMessage.toString()));
        return new SerializedKVMessage(
                KVMessageUtils.convertMessageToString(unserializedMessage).getBytes());
    }
}
